package AlkemyWallet.AlkemyWallet.controllers;
import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.domain.Transaction;
import AlkemyWallet.AlkemyWallet.dtos.TransactionBalanceDTO;
import AlkemyWallet.AlkemyWallet.dtos.TransactionDTO;
import AlkemyWallet.AlkemyWallet.services.AccountService;
import AlkemyWallet.AlkemyWallet.services.JwtService;
import AlkemyWallet.AlkemyWallet.services.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
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


    @PostMapping({"/sendArs", "/sendUsd"})
    public ResponseEntity<?> sendMoney(@Valid @RequestBody TransactionDTO transaction, HttpServletRequest request) {
        String token = jwtService.getTokenFromRequest(request);
        Accounts account = accountService.getAccountFrom(token);
        return ResponseEntity.ok(transactionService.registrarTransaccion(transaction, account));
    }
    @GetMapping("/{userId}")
    /* @PreAuthorize("hasRole('ADMIN')") */
    public ResponseEntity<?> getTransactionsByUserId(@PathVariable Long userId) {
        try {
            List<Accounts> accounts = accountService.findAccountsByUserId(userId);
            List<TransactionBalanceDTO> transactionsDtos = new ArrayList<>();

            for (Accounts account : accounts) {
                List<Transaction> accountTransactions = transactionService.getTransactionsByAccountId(account.getId());
                for (Transaction transaction : accountTransactions) {
                    TransactionBalanceDTO dto = new TransactionBalanceDTO();
                    dto.setId(transaction.getId());
                    dto.setAmount(transaction.getAmount());
                    dto.setTransactionDate(transaction.getTransactionDate());
                    dto.setDescription(transaction.getDescription());
                    dto.setType(transaction.getType());
                    dto.setCurrency(transaction.getOriginAccount().getCurrency().toString());
                    dto.setOriginAccountCBU(transaction.getOriginAccount().getCBU());
                    transactionsDtos.add(dto);
                }
            }
            return ResponseEntity.ok(transactionsDtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al encontrar las transacciones del usuario: " + e.getMessage());
        }
    }
}


