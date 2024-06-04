package AlkemyWallet.AlkemyWallet.controllers;

import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.domain.Loan;
import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.dtos.FixedTermDepositDto;
import AlkemyWallet.AlkemyWallet.dtos.LoanRequestDTO;
import AlkemyWallet.AlkemyWallet.dtos.LoanResponseDTO;
import AlkemyWallet.AlkemyWallet.services.AccountService;
import AlkemyWallet.AlkemyWallet.services.FixedTermDepositService;
import AlkemyWallet.AlkemyWallet.services.JwtService;
import AlkemyWallet.AlkemyWallet.services.LoanService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/fixedTerm")
public class FixedTermDepositController {
    private final FixedTermDepositService fixedTermDepositService;
    private final JwtService jwtService;
    private final AccountService accountService;

    @PostMapping("/simulate")
    public ResponseEntity<?> simulateFixedTermDeposit(@Valid @RequestBody FixedTermDepositDto fixedTermDepositDto){
        try {
            return ResponseEntity.ok(fixedTermDepositService.simulateFixedTermDeposit(fixedTermDepositDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al simular plazo fijo: " + e.getMessage());
        }
    }

    @PostMapping("/fixedTerm")
    public ResponseEntity<?> createFixedTermDeposit(@Valid @RequestBody FixedTermDepositDto fixedTermDepositDto, HttpServletRequest request){
        String token = jwtService.getTokenFromRequest(request);
        Accounts account = accountService.getAccountFrom(token);
        User user = jwtService.getUserFromToken(token);

        //Tengo que devolver el token sin la nueva info de cuenta
        token = jwtService.removeAccountIdFromToken(token);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        return  ResponseEntity.ok(fixedTermDepositService.fixedTermDeposit(fixedTermDepositDto, account, user));
    }
}
