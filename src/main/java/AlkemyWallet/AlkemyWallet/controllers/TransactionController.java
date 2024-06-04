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
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


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

    @GetMapping("user/{userId}")
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
    @GetMapping("/{transactionId}")
    public ResponseEntity<?> getTransactionById(@PathVariable Long transactionId) {
        try {
            Transaction transaction = transactionService.getTransactionById(transactionId);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al obtener la transacción: " + e.getMessage());
        }
    }
}
