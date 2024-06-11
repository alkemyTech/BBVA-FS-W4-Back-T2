package AlkemyWallet.AlkemyWallet.controllers;

import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.dtos.BalanceDTO;
import AlkemyWallet.AlkemyWallet.dtos.AccountRequestDto;
import AlkemyWallet.AlkemyWallet.exceptions.CuentaNotFoundException;
import AlkemyWallet.AlkemyWallet.exceptions.LimiteTransaccionExcedidoException;
import AlkemyWallet.AlkemyWallet.exceptions.UnauthorizedAccountAccessException;
import AlkemyWallet.AlkemyWallet.repositories.UserRepository;
import AlkemyWallet.AlkemyWallet.services.AccountService;
import AlkemyWallet.AlkemyWallet.services.BalanceService;
import AlkemyWallet.AlkemyWallet.services.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;
    private final BalanceService balanceService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Autowired
    public AccountController(AccountService accountService, BalanceService balanceService, JwtService jwtService, UserRepository userRepository) {
        this.accountService = accountService;
        this.balanceService = balanceService;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Operation(
            description = "Obtiene una lista de todas las cuentas con paginación",
            summary = "Obtener todas las cuentas",
            responses = {
                    @ApiResponse(
                            description = "Cuentas obtenidas con éxito",
                            responseCode = "200",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            description = "Error al obtener las cuentas",
                            responseCode = "500",
                            content = @Content(mediaType = "text/plain")
                    )
            }
    )
    @GetMapping("")
    public ResponseEntity<?> getAccounts(@RequestParam(defaultValue = "0") int page) {
        try {
            Page<Accounts> accountsPage = accountService.getAllAccounts(page);
            int totalPages = accountsPage.getTotalPages();

            Map<String, Object> response = new HashMap<>();
            response.put("accounts", accountsPage.getContent());
            response.put("currentPage", page);
            response.put("totalPages", totalPages);

            if (page < totalPages - 1) {
                response.put("nextPage", "/accounts?page=" + (page + 1));
            }
            if (page > 0) {
                response.put("previousPage", "/accounts?page=" + (page - 1));
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener todas las cuentas: " + e.getMessage());
        }
    }

    @Operation(
            description = "Crea una nueva cuenta",
            summary = "Crear cuenta",
            responses = {
                    @ApiResponse(
                            description = "Cuenta creada con éxito",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = Accounts.class), mediaType = "application/json")
                    ),
                    @ApiResponse(
                            description = "Error en la creación de la cuenta",
                            responseCode = "500",
                            content = @Content(mediaType = "text/plain")
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
            description = "Actualiza el límite de transacción de una cuenta",
            summary = "Actualizar cuenta",
            responses = {
                    @ApiResponse(
                            description = "Cuenta actualizada con éxito",
                            responseCode = "200",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            description = "Error al actualizar la cuenta",
                            responseCode = "404",
                            content = @Content(mediaType = "text/plain")
                    )
            }
    )
    @PatchMapping("/editar/{accountId}")
    public ResponseEntity<?> updateAccount(@PathVariable Long accountId, @RequestBody Double transactionLimit) {
        try {

            return ResponseEntity.ok(accountService.updateAccount(accountId, transactionLimit));

        } catch (CuentaNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al actualizar cuenta: " + e.getMessage());
        } catch (LimiteTransaccionExcedidoException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al actualizar cuenta: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar cuenta: " + e.getMessage());
        }
    }

    @Operation(
            description = "Obtiene las cuentas por el ID del usuario",
            summary = "Obtener cuentas por ID de usuario",
            responses = {
                    @ApiResponse(
                            description = "Cuentas obtenidas con éxito",
                            responseCode = "200",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            description = "Usuario no encontrado",
                            responseCode = "404",
                            content = @Content(mediaType = "text/plain")
                    )
            }
    )
    @GetMapping("/{userId}")
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
                            content = @Content(mediaType = "text/plain")
                    )
            }
    )

    @PostMapping("/select/{accountId}")
    public ResponseEntity<String> selectAccount(HttpServletRequest request, @PathVariable Long accountId) {
        try {
            // Obtener el token actual del usuario autenticado
            String currentToken = jwtService.getTokenFromRequest(request);

            User userAccount = accountService.findById(accountId).getUserId();

            if (jwtService.getUsernameFromToken(currentToken).equals(userAccount.getUsername())) {
                // Agregar el ID de cuenta al token
                String accountIdAdd = String.valueOf(accountId);
                String updatedToken = jwtService.addAccountIdToToken(currentToken, accountIdAdd);

                // Para usar header
                HttpHeaders headers = new HttpHeaders();
                headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + updatedToken);

                // Devolver el nuevo token en la respuesta
                return ResponseEntity.ok().headers(headers).body("Token actualizado con éxito");
            } else {
                throw new UnauthorizedAccountAccessException("No está autorizado para acceder a esta cuenta");
            }
        } catch (UnauthorizedAccountAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            // Manejar cualquier otro error que pueda ocurrir
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar la solicitud: " + e.getMessage());
        }
    }

    @Operation(
            description = "Obtiene el balance del usuario y sus transacciones",
            summary = "Obtener balance y transacciones del usuario",
            responses = {
                    @ApiResponse(
                            description = "Balance obtenido con éxito",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = BalanceDTO.class), mediaType = "application/json")
                    ),
                    @ApiResponse(
                            description = "Usuario no encontrado",
                            responseCode = "404",
                            content = @Content(mediaType = "text/plain")
                    ),
                    @ApiResponse(
                            description = "Token ha expirado",
                            responseCode = "401",
                            content = @Content(mediaType = "text/plain")
                    ),
                    @ApiResponse(
                            description = "Error al procesar la solicitud",
                            responseCode = "500",
                            content = @Content(mediaType = "text/plain")
                    )
            }
    )
    @GetMapping("/balance")
    public ResponseEntity<?> getBalance(HttpServletRequest request) {
        try {
            String token = jwtService.getTokenFromRequest(request);
            String username = jwtService.getUsernameFromToken(token);
            Optional<User> userOptional = userRepository.findByUserName(username);

            if (userOptional.isPresent()) {
                Long userId = userOptional.get().getId();
                BalanceDTO balanceDTO = balanceService.getUserBalanceAndTransactions(userId);
                return ResponseEntity.ok(balanceDTO);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
            }
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token ha expirado");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar la solicitud");
        }
    }
}
