package AlkemyWallet.AlkemyWallet.controllers;
import AlkemyWallet.AlkemyWallet.dtos.TransactionDTO;
import AlkemyWallet.AlkemyWallet.services.TransactionService;

import AlkemyWallet.AlkemyWallet.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;
    private UserService userService;

    @PostMapping({"/sendArs", "/sendUsd"})
    public ResponseEntity<?>  sendMoney(@RequestBody TransactionDTO transaction, HttpServletRequest request) {
        return ResponseEntity.ok(transactionService.registrarTransaccion(transaction));
    }

    @GetMapping("/accounts")
    public ResponseEntity<?> getUserAccounts(HttpServletRequest request) {
        Long userId = userService.getIdFromRequest(request);
        //List<Accounts> accounts = userService.getAccountsByUserId(Long);
        //return ResponseEntity.ok(accounts);
        return null;
    }


}
