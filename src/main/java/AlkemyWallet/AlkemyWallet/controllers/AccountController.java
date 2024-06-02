package AlkemyWallet.AlkemyWallet.controllers;

import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.dtos.CurrencyDto;
import AlkemyWallet.AlkemyWallet.enums.CurrencyEnum;
import AlkemyWallet.AlkemyWallet.security.JwtAuthenticationFilter;
import AlkemyWallet.AlkemyWallet.services.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public AccountController(AccountService accountService, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.accountService = accountService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createAccount(@Valid @RequestBody CurrencyDto currency, HttpServletRequest request) {
        try {
            Accounts account = accountService.add(currency, request);
            return ResponseEntity.ok(account);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear la cuenta: " + e.getMessage());
        }
    }

    @GetMapping("/{userId}")
    /*@PreAuthorize("hasRole('ADMIN')")*/
    public ResponseEntity<?> getAccountsByUserId(@PathVariable Long userId) {
        try {
            List<Accounts> accounts = accountService.findAccountsByUserId(userId);
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al encontrar las cuentas del usuario: " + e.getMessage());
        }
    }

}