package AlkemyWallet.AlkemyWallet.controllers;

import AlkemyWallet.AlkemyWallet.domain.Accounts;

import AlkemyWallet.AlkemyWallet.dtos.CurrencyDto;
import AlkemyWallet.AlkemyWallet.enums.CurrencyEnum;
import AlkemyWallet.AlkemyWallet.security.JwtAuthenticationFilter;
import AlkemyWallet.AlkemyWallet.services.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public Accounts createAccount(@Valid @RequestBody CurrencyDto currency,
                                  HttpServletRequest request) {
        // Extraer el token utilizando el método existente

//        if (token == null) {
//            throw new IllegalArgumentException("Token de autorización no encontrado o inválido");
//        }

        return accountService.add(currency,request);
    }
  
  @GetMapping("/{userId}")
    /*@PreAuthorize("hasRole('ADMIN')")*/
    public ResponseEntity<List<Accounts>> getAccountsByUserId(@PathVariable Long userId) {
        List<Accounts> accounts = accountService.findAccountsByUserId(userId);
        return ResponseEntity.ok(accounts);
    }


}