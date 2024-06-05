package AlkemyWallet.AlkemyWallet.controllers;

import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.dtos.FixedTermDepositDto;
import AlkemyWallet.AlkemyWallet.services.AccountService;
import AlkemyWallet.AlkemyWallet.services.FixedTermDepositService;
import AlkemyWallet.AlkemyWallet.services.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/fixedTerm")
public class FixedTermDepositController {
    private final FixedTermDepositService fixedTermDepositService;
    private final JwtService jwtService;
    private final AccountService accountService;

    @Operation(
            description = "Simula un depósito a plazo fijo",
            summary = "Simular depósito a plazo fijo",
            responses = {
                    @ApiResponse(
                            description = "Simulación realizada con éxito",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = FixedTermDepositDto.class), mediaType = "application/json")
                    ),
                    @ApiResponse(
                            description = "Error en la simulación del plazo fijo",
                            responseCode = "500",
                            content = @Content(schema = @Schema(implementation = String.class), mediaType = "text/plain")
                    )
            }
    )
    @PostMapping("/simulate")
    public ResponseEntity<?> simulateFixedTermDeposit(@Valid @RequestBody FixedTermDepositDto fixedTermDepositDto){
        try {
            return ResponseEntity.ok(fixedTermDepositService.simulateFixedTermDeposit(fixedTermDepositDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al simular plazo fijo: " + e.getMessage());
        }
    }

    @Operation(
            description = "Crea un depósito a plazo fijo",
            summary = "Crear depósito a plazo fijo",
            responses = {
                    @ApiResponse(
                            description = "Depósito a plazo fijo creado con éxito",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = FixedTermDepositDto.class), mediaType = "application/json")
                    ),
                    @ApiResponse(
                            description = "Error al crear el depósito a plazo fijo",
                            responseCode = "500",
                            content = @Content(schema = @Schema(implementation = String.class), mediaType = "text/plain")
                    )
            }
    )
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
