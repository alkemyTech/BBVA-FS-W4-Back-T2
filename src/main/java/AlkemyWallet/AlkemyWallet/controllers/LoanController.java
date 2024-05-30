package AlkemyWallet.AlkemyWallet.controllers;

import AlkemyWallet.AlkemyWallet.domain.Loan;
import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.dtos.LoanDTO;
import AlkemyWallet.AlkemyWallet.services.LoanService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/loan")
public class LoanController {
    private final LoanService loanService;

    @PostMapping("/simulate")
    public ResponseEntity<?> simulateLoan(@Valid @RequestBody LoanDTO loanDTO){

        try {
            Loan loan = loanService.simulateLoan(loanDTO);
            return ResponseEntity.ok(loan);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al simular prestamo: " + e.getMessage());
        }
    }
}
