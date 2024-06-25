package AlkemyWallet.AlkemyWallet.controllers;

import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.domain.Transaction;
import AlkemyWallet.AlkemyWallet.domain.TransactionFilter;
import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.dtos.PaymentResponseDTO;
import AlkemyWallet.AlkemyWallet.dtos.TransactionDTO;
import AlkemyWallet.AlkemyWallet.dtos.TransactionResponse;
import AlkemyWallet.AlkemyWallet.exceptions.IncorrectCurrencyException;
import AlkemyWallet.AlkemyWallet.exceptions.InsufficientFundsException;
import AlkemyWallet.AlkemyWallet.repositories.UserRepository;
import AlkemyWallet.AlkemyWallet.services.AccountService;
import AlkemyWallet.AlkemyWallet.services.JwtService;
import AlkemyWallet.AlkemyWallet.services.TransactionService;
import AlkemyWallet.AlkemyWallet.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/transactions")
@AllArgsConstructor
@Validated
public class TransactionController {

    private final TransactionService transactionService;
    private final AccountService accountService;
    private final JwtService jwtService;
    private final UserService userService;

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

    @PostMapping({"/sendArs", "/sendUsd"})
    public ResponseEntity<?> sendMoney(@Valid @RequestBody TransactionDTO transaction, HttpServletRequest request) {
        try {
            String token = jwtService.getTokenFromRequest(request);
            Accounts account = accountService.getAccountFrom(token);

            // Lógica para registrar la transacción
            TransactionResponse response = transactionService.registrarTransaccion(transaction, account);

            // Devolver una respuesta exitosa
            return ResponseEntity.ok().headers(createHeadersWithUpdatedToken(token)).body(response);
        } catch (InsufficientFundsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: No hay suficientes fondos para completar la transacción");
        } catch (IncorrectCurrencyException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: La moneda seleccionada no es la correcta para este tipo de cuenta");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error genérico: " + e.getMessage());
        }
    }

    @PostMapping({ "/payment"})
    public ResponseEntity<?> payment(@Valid @RequestBody TransactionDTO transaction, HttpServletRequest request) {
        try {
            String token = jwtService.getTokenFromRequest(request);
            Accounts account = accountService.getAccountFrom(token);

            // Lógica para registrar la transacción
            PaymentResponseDTO response = transactionService.registrarPago(transaction, account);

            // Devolver una respuesta exitosa
            return ResponseEntity.ok().headers(createHeadersWithUpdatedToken(token)).body(response);
        } catch (InsufficientFundsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: No hay suficientes fondos para completar la transacción");
        } catch (IncorrectCurrencyException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: La moneda seleccionada no es la correcta para este tipo de cuenta");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error genérico: " + e.getMessage());
        }
    }

    private HttpHeaders createHeadersWithUpdatedToken(String currentToken) {
        String updatedToken = jwtService.removeAccountIdFromToken(currentToken);
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + updatedToken);
        return headers;
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

    @GetMapping("/filter")
    public ResponseEntity<?> getPagedTransactionsForUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) String transactionType,
            @RequestParam(required = false) String currency,
            HttpServletRequest request) {
        try {
            String token = jwtService.getTokenFromRequest(request);
            String username = jwtService.getUsernameFromToken(token);

            Optional<User> userOptional = userService.getUserByUsername(username);

            Long userId = null;
            if (userOptional.isPresent()) {
                userId = userOptional.get().getId();
            }

            // Construir objeto de filtros basado en los parámetros recibidos
            TransactionFilter filter = new TransactionFilter();
            filter.setUserId(userId);
            filter.setPage(page);

            if (fromDate != null) {
                filter.setFromDate(LocalDate.parse(fromDate));
            }
            if (toDate != null) {
                filter.setToDate(LocalDate.parse(toDate));
            }
            if (transactionType != null) {
                filter.setTransactionType(transactionType);
            }
            if (currency != null) {
                filter.setCurrency(currency);
            }

            // Obtener página de transacciones utilizando el servicio
            Page<TransactionResponse> transactionsPage = transactionService.getTransactionsWithFilters(filter);
            int totalPages = transactionsPage.getTotalPages();

            // Construir la respuesta
            Map<String, Object> response = new HashMap<>();
            response.put("transactions", transactionsPage.getContent());
            response.put("currentPage", page);
            response.put("totalPages", totalPages);

            if (page < totalPages - 1) {
                response.put("nextPage", "?page=" + (page + 1));
            }
            if (page > 0) {
                response.put("previousPage", "?page=" + (page - 1));
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al encontrar las transacciones del usuario: " + e.getMessage());
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
