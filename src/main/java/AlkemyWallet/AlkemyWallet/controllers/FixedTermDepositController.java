package AlkemyWallet.AlkemyWallet.controllers;

import AlkemyWallet.AlkemyWallet.domain.Loan;
import AlkemyWallet.AlkemyWallet.dtos.FixedTermDepositDto;
import AlkemyWallet.AlkemyWallet.dtos.LoanRequestDTO;
import AlkemyWallet.AlkemyWallet.dtos.LoanResponseDTO;
import AlkemyWallet.AlkemyWallet.services.FixedTermDepositService;
import AlkemyWallet.AlkemyWallet.services.LoanService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/fixedTerm")
public class FixedTermDepositController {
    private final FixedTermDepositService fixedTermDepositService;

    @PostMapping("/simulate")
    public ResponseEntity<?> simulateLoan(@Valid @RequestBody FixedTermDepositDto fixedTermDepositDto){
        try {
            return ResponseEntity.ok(fixedTermDepositService.simulateFixedTermDeposit(fixedTermDepositDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al simular plazo fijo: " + e.getMessage());
        }
    }
}
