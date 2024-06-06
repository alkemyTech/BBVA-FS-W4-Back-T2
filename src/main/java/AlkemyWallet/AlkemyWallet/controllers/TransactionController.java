package AlkemyWallet.AlkemyWallet.controllers;

import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.domain.Transaction;
import AlkemyWallet.AlkemyWallet.domain.User;
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
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/transactions")
@AllArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final AccountService accountService;
    private final JwtService jwtService;

    @Operation(
            description = "Realiza un envío de dinero ya sea en pesos o USD",
            summary = "Enviar dinero",
            responses = {
                    @ApiResponse(
                            description = "Transacción realizada con éxito",
                            responseCode = "201",
                            content = @Content(schema = @Schema(implementation = Transaction.class), mediaType = "application/json")
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

        // Devolver el token sin la nueva info de cuenta
        token = jwtService.removeAccountIdFromToken(token);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        return ResponseEntity.ok().headers(headers).body(transactionService.registrarTransaccion(transaction, account));
    }

    @Operation(
            description = "Realiza un depósito en su cuenta",
            summary = "Depositar dinero",
            responses = {
                    @ApiResponse(
                            description = "Depósito realizado con éxito",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = Transaction.class), mediaType = "application/json")
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
            description = "Devuelve un listado de transacciones por usuario",
            summary = "Obtener transacciones por ID de usuario",
            responses = {
                    @ApiResponse(
                            description = "Listado de transacciones obtenido con éxito",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = Transaction.class), mediaType = "application/json")
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
    public ResponseEntity<?> getPagedTransactions(@PathVariable Long userId, @RequestParam(defaultValue = "0") int page) {
        try {
            Page<Transaction> transactionsPage = transactionService.getTransactionsByUserIdPaginated(userId, page);
            int totalPages = transactionsPage.getTotalPages();

            Map<String, Object> response = new HashMap<>();
            response.put("transactions", transactionsPage.getContent());
            response.put("currentPage", page);
            response.put("totalPages", totalPages);

            if (page < totalPages - 1) {
                response.put("nextPage", "/admin/" + userId + "?page=" + (page + 1));
            }
            if (page > 0) {
                response.put("previousPage", "/admin/" + userId + "?page=" + (page - 1));
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al encontrar las transacciones del usuario: " + e.getMessage());


        }

    }

    @Operation(
            description = "Devuelve la transacción por ID",
            summary = "Obtener transacción por ID",
            responses = {
                    @ApiResponse(
                            description = "Transacción obtenida con éxito",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = Transaction.class), mediaType = "application/json")
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

    @Operation(
            description = "Actualiza la descripción de una transacción",
            summary = "Actualizar descripción de la transacción",
            responses = {
                    @ApiResponse(
                            description = "Descripción actualizada con éxito",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = Transaction.class), mediaType = "application/json")
                    ),
                    @ApiResponse(
                            description = "Error al actualizar la transacción",
                            responseCode = "403",
                            content = @Content(schema = @Schema(implementation = String.class), mediaType = "text/plain")
                    )
            }
    )
    @PatchMapping("detail/{id}")
    public ResponseEntity<?> updateTransactionDescription(@PathVariable Long id, @RequestBody String description) {
        try {
            // Obtener el userId del usuario autenticado
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User authenticatedUser = (User) authentication.getPrincipal();
            Long userId = authenticatedUser.getId();

            // Actualizar la descripción de la transacción
            Transaction transaction = transactionService.updateTransactionDescription(id, description, userId);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error al actualizar la transacción: " + e.getMessage());
        }
    }


}
