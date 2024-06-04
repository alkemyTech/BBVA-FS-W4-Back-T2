package AlkemyWallet.AlkemyWallet.controllers;
import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.domain.Transaction;
import AlkemyWallet.AlkemyWallet.dtos.TransactionDTO;
import AlkemyWallet.AlkemyWallet.services.AccountService;
import AlkemyWallet.AlkemyWallet.services.JwtService;
import AlkemyWallet.AlkemyWallet.services.TransactionService;

import AlkemyWallet.AlkemyWallet.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/transactions")
@AllArgsConstructor
public class TransactionController {


    private TransactionService transactionService;
    private AccountService accountService;
    private JwtService jwtService;


    @PostMapping({"/sendArs", "/sendUsd", "/payment"})
    public ResponseEntity<?> sendMoney(@Valid @RequestBody TransactionDTO transaction, HttpServletRequest request) {
        String token = jwtService.getTokenFromRequest(request);
        Accounts account = accountService.getAccountFrom(token);


        //Tengo que devolver el token sin la nueva info de cuenta
        token = jwtService.removeAccountIdFromToken(token);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        return ResponseEntity.ok().headers(headers).body(transactionService.registrarTransaccion(transaction,account));
    }

    @PostMapping({"/deposit"})
    public ResponseEntity<?> depositMoney(@Valid @RequestBody TransactionDTO transaction, HttpServletRequest request) {
        String token = jwtService.getTokenFromRequest(request);
        Accounts account = accountService.getAccountFrom(token);
        return ResponseEntity.ok(transactionService.depositMoney(transaction, account));
    }

    /*
     public ResponseEntity<?> getUsers(@RequestParam(defaultValue = "0") int page) {
        try {
            Page<User> users = userService.getAllUsers(page);
            return ResponseEntity.ok(users);
            Page<User> usersPage = userService.getAllUsers(page);
            int totalPages = usersPage.getTotalPages();

            Map<String, Object> response = new HashMap<>();
            response.put("users", usersPage.getContent());
            response.put("currentPage", page);
            response.put("totalPages", totalPages);

            if (page < totalPages - 1) {
                response.put("nextPage", "/users?page=" + (page + 1));
            }
            if (page > 0) {
                response.put("previousPage", "/users?page=" + (page - 1));
            }

            return ResponseEntity.ok(response);
     */

    @GetMapping("/admin/{userId}")
    public ResponseEntity<?> getPagedTransactions(@PathVariable Long userId, @RequestParam(defaultValue = "0") int page) {
        try {
            Page<Transaction> transactionsPage = transactionService.getTransactionsByUserIdPaginated(userId, page);
            int totalPages = transactionsPage.getTotalPages();

            Map<String, Object> response = new HashMap<>();
            response.put("transactions", transactionsPage.getContent());
            response.put("currentPage", page);
            response.put("totalPages", totalPages);

            if (page < totalPages - 1) {
                response.put("nextPage", "/admin/" + userId + "?page=" + (page + 1));
            }
            if (page > 0) {
                response.put("previousPage", "/admin/" + userId + "?page=" + (page - 1));
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al encontrar las transacciones del usuario: " + e.getMessage());


        }

    }

    @GetMapping("user/{userId}")
    /* @PreAuthorize("hasRole('ADMIN')") */
    public ResponseEntity<?> getTransactionsByUserId(@PathVariable Long userId) {
        try {
            List<Accounts> accounts = accountService.findAccountsByUserId(userId);
            List<Transaction> transactions = new ArrayList<>();

            for (Accounts account : accounts) {
                transactions.addAll(transactionService.getTransactionsByAccountId(account.getId()));
            }
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al encontrar las transacciones del usuario: " + e.getMessage());


        }

    }
    @GetMapping("transaction/{transactionId}")
    public ResponseEntity<?> getTransactionById(@PathVariable Long transactionId) {
        try {
            Transaction transaction = transactionService.getTransactionById(transactionId);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al obtener la transacción: " + e.getMessage());
        }
    }
}
