package AlkemyWallet.AlkemyWallet.controllers;

import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.domain.Transaction;
import AlkemyWallet.AlkemyWallet.dtos.TransactionDTO;
import AlkemyWallet.AlkemyWallet.services.AccountService;
import AlkemyWallet.AlkemyWallet.services.JwtService;
import AlkemyWallet.AlkemyWallet.services.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/transactions")
@AllArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final AccountService accountService;
    private final JwtService jwtService;

    @Operation(
            description = "Endpoint accesible a usuarios autenticados",
            summary = "Realiza un envío de dinero ya sea en pesos o USD",
            responses = {
                    @ApiResponse(
                            description = "Created",
                            responseCode = "201",
                            content = {
                                    @Content(schema = @Schema(implementation = Transaction.class), mediaType = "application/json")
                            }
                    ),
                    @ApiResponse(
                            description = "Error en el envío de dinero",
                            responseCode = "500",
                            content = @Content(schema = @Schema(implementation = String.class), mediaType = "text/plain")
                    )
            }
    )
    @PostMapping({"/sendArs", "/sendUsd", "/payment"})
    public ResponseEntity<?> sendMoney(@Valid @RequestBody TransactionDTO transaction, HttpServletRequest request) {
        String token = jwtService.getTokenFromRequest(request);
        Accounts account = accountService.getAccountFrom(token);

        // Tengo que devolver el token sin la nueva info de cuenta
        token = jwtService.removeAccountIdFromToken(token);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        return ResponseEntity.ok().headers(headers).body(transactionService.registrarTransaccion(transaction, account));
    }

    @Operation(
            description = "Endpoint accesible a usuarios autenticados",
            summary = "Realiza un depósito en su cuenta",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {
                                    @Content(schema = @Schema(implementation = Transaction.class), mediaType = "application/json")
                            }
                    ),
                    @ApiResponse(
                            description = "Error en el depósito",
                            responseCode = "500",
                            content = @Content(schema = @Schema(implementation = String.class), mediaType = "text/plain")
                    )
            }
    )
    @PostMapping("/deposit")
    public ResponseEntity<?> depositMoney(@Valid @RequestBody TransactionDTO transaction, HttpServletRequest request) {
        String token = jwtService.getTokenFromRequest(request);
        Accounts account = accountService.getAccountFrom(token);
        return ResponseEntity.ok(transactionService.depositMoney(transaction, account));
    }

    @Operation(
            description = "Endpoint accesible a usuarios Admin",
            summary = "Devuelve un listado de transacciones por usuario",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {
                                    @Content(schema = @Schema(implementation = Transaction.class), mediaType = "application/json")
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
    @GetMapping("user/{userId}")
    /* @PreAuthorize("hasRole('ADMIN')") */
    public ResponseEntity<?> getTransactionsByUserId(@PathVariable Long userId) {
        try {
            List<Accounts> accounts = accountService.findAccountsByUserId(userId);
            List<Transaction> transactions = new ArrayList<>();

            for (Accounts account : accounts) {
                transactions.addAll(transactionService.getTransactionsByAccountId(account.getId()));
            }
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al encontrar las transacciones del usuario: " + e.getMessage());
        }
    }

    @Operation(
            description = "Endpoint accesible a usuarios autenticados",
            summary = "Devuelve la transacción por id",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {
                                    @Content(schema = @Schema(implementation = Transaction.class), mediaType = "application/json")
                            }
                    ),
                    @ApiResponse(
                            description = "Transacción no encontrada",
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
    @GetMapping("transaction/{transactionId}")
    public ResponseEntity<?> getTransactionById(@PathVariable Long transactionId) {
        try {
            Transaction transaction = transactionService.getTransactionById(transactionId);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al obtener la transacción: " + e.getMessage());
        }
    }
}
