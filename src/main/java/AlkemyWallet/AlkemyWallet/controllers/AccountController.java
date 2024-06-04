package AlkemyWallet.AlkemyWallet.controllers;

import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.dtos.AccountRequestDto;
import AlkemyWallet.AlkemyWallet.services.AccountService;
import AlkemyWallet.AlkemyWallet.services.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;
    private final JwtService jwtService;

    @Autowired
    public AccountController(AccountService accountService, JwtService jwtService) {
        this.accountService = accountService;
        this.jwtService = jwtService;
    }

    @Operation(
            description = "Crea una nueva cuenta",
            summary = "Crear cuenta",
            responses = {
                    @ApiResponse(
                            description = "Cuenta creada con éxito",
                            responseCode = "200",
                            content = {
                                    @Content(schema = @Schema(implementation = Accounts.class), mediaType = "application/json")
                            }
                    ),
                    @ApiResponse(
                            description = "Error en la creación de la cuenta",
                            responseCode = "500",
                            content = @Content(schema = @Schema(implementation = String.class), mediaType = "text/plain")
                    )
            }
    )
    @PostMapping("/create")
    public ResponseEntity<?> createAccount(@Valid @RequestBody AccountRequestDto accountCreation, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(accountService.add(accountCreation, request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear la cuenta: " + e.getMessage());
        }
    }

    @Operation(
            description = "Selecciona una cuenta y actualiza el limite de transaccion",
            summary = "Seleccionar cuenta y actualizar limite de transaccion",
            responses = {
                    @ApiResponse(
                            description = "Se cambio el limite de la transaccion para esta cuenta",
                            responseCode = "200",
                            content = {
                                    @Content(schema = @Schema(implementation = Accounts.class), mediaType = "application/json")
                            }
                    ),
                    @ApiResponse(
                            description = "Error al procesar la solicitud",
                            responseCode = "500",
                            content = @Content(schema = @Schema(implementation = String.class), mediaType = "text/plain")
                    )
            }
    )

    @PatchMapping("/{accountId}")
    public ResponseEntity<?> updateAccount(@PathVariable Long accountId, @RequestBody Double transactionLimit) {
        try {
            return ResponseEntity.ok(accountService.updateAccount(accountId,transactionLimit));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al actualizar cuenta: " + e.getMessage());
        }
    }

    @Operation(
            description = "Endpoint accesible a admins",
            summary = "Traer cuentas por id de usuario",
            responses = {
                    @ApiResponse(
                            description = "Éxito",
                            responseCode = "200",
                            content = {
                                    @Content(schema = @Schema(implementation = Accounts.class), mediaType = "application/json")
                            }
                    ),
                    @ApiResponse(
                            description = "Usuario no encontrado",
                            responseCode = "404",
                            content = @Content(schema = @Schema(implementation = String.class), mediaType = "text/plain")
                    ),
                    @ApiResponse(
                            description = "No autenticado / Token inválido",
                            responseCode = "403",
                            content = @Content
                    )
            }
    )
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

    @Operation(
            description = "Selecciona una cuenta y actualiza el token JWT",
            summary = "Seleccionar cuenta y actualizar token",
            responses = {
                    @ApiResponse(
                            description = "Token actualizado con éxito",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = String.class), mediaType = "text/plain")
                    ),
                    @ApiResponse(
                            description = "Error al procesar la solicitud",
                            responseCode = "500",
                            content = @Content(schema = @Schema(implementation = String.class), mediaType = "text/plain")
                    )
            }
    )
    @PostMapping("/select/{accountId}")
    public ResponseEntity<String> selectAccount(HttpServletRequest request, @PathVariable Long accountId) {
        try {
            // Obtener el token actual del usuario autenticado
            String currentToken = jwtService.getTokenFromRequest(request);

            // Agregar el ID de cuenta al token
            String accountIdAdd = String.valueOf(accountId);
            String updatedToken = jwtService.addAccountIdToToken(currentToken, accountIdAdd);

            // Para usar header
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + updatedToken);

            // Devolver el nuevo token en la respuesta
            return ResponseEntity.ok().headers(headers).body("Token actualizado con éxito");
        } catch (Exception e) {
            // Manejar cualquier error que pueda ocurrir
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar la solicitud");
        }
    }
}
