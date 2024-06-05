package AlkemyWallet.AlkemyWallet.controllers;

import AlkemyWallet.AlkemyWallet.dtos.LoanRequestDTO;
import AlkemyWallet.AlkemyWallet.dtos.LoanResponseDTO;
import AlkemyWallet.AlkemyWallet.services.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/loan")
public class LoanController {
    private final LoanService loanService;

    @Operation(
            description = "Endpoint accesible a usuarios autenticados",
            summary = "Genera un simulacro de préstamo",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {
                                    @Content(schema = @Schema(implementation = LoanResponseDTO.class), mediaType = "application/json")
                            }
                    ),
                    @ApiResponse(
                            description = "Error al simular préstamo",
                            responseCode = "500",
                            content = @Content(schema = @Schema(implementation = String.class), mediaType = "text/plain")
                    )
            }
    )
    @PostMapping("/simulate")
    public ResponseEntity<?> simulateLoan(@Valid @RequestBody LoanRequestDTO loanRequestDTO){
        try {
            LoanResponseDTO loan = loanService.simulateLoan(loanRequestDTO);
            return ResponseEntity.ok(loan);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al simular préstamo: " + e.getMessage());
        }
    }
}
