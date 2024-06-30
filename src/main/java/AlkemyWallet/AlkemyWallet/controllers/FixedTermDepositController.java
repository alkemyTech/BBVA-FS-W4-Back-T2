package AlkemyWallet.AlkemyWallet.controllers;

import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.domain.FixedTermDeposit;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/fixedTerm")
@Validated
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

    @PostMapping()
    public ResponseEntity<?> createFixedTermDeposit(@Valid @RequestBody FixedTermDepositDto fixedTermDepositDto, HttpServletRequest request){
        String token = jwtService.getTokenFromRequest(request);
        Accounts account = accountService.getAccountFrom(token);
        User user = jwtService.getUserFromToken(token);

        //Tengo que devolver el token sin la nueva info de cuenta
        token = jwtService.removeAccountIdFromToken(token);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        return  ResponseEntity.ok().headers(headers).body(fixedTermDepositService.fixedTermDeposit(fixedTermDepositDto, account, user));
    }

    @Operation(
            description = "Obtiene todos los movimientos de plazos fijos del usuario autenticado",
            summary = "Obtener movimientos de plazos fijos",
            responses = {
                    @ApiResponse(
                            description = "Movimientos obtenidos con éxito",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = FixedTermDepositDto.class), mediaType = "application/json")
                    ),
                    @ApiResponse(
                            description = "Error al obtener los movimientos",
                            responseCode = "500",
                            content = @Content(schema = @Schema(implementation = String.class), mediaType = "text/plain")
                    )
            }
    )
    @GetMapping("/all")
    public ResponseEntity<?> getFixedTermMovements(HttpServletRequest request) {
        try {
            String token = jwtService.getTokenFromRequest(request);
            User user = jwtService.getUserFromToken(token);
            List<FixedTermDeposit> fixedTermDeposits = fixedTermDepositService.getFixedTermDepositsByUser(user.getId());
            return ResponseEntity.ok(fixedTermDeposits);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener los movimientos de plazos fijos: " + e.getMessage());
        }
    }
}
