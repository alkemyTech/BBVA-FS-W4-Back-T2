package AlkemyWallet.AlkemyWallet.controllers;
import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.dtos.CurrencyDto;
import AlkemyWallet.AlkemyWallet.enums.CurrencyEnum;
import AlkemyWallet.AlkemyWallet.dtos.AccountRequestDto;
import AlkemyWallet.AlkemyWallet.security.JwtAuthenticationFilter;
import org.springframework.http.HttpHeaders;
import AlkemyWallet.AlkemyWallet.services.AccountService;
import AlkemyWallet.AlkemyWallet.services.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener todos las cuentas: " + e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createAccount(@Valid @RequestBody AccountRequestDto accountCreation, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(accountService.add(accountCreation, request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear la cuenta: " + e.getMessage());
        }
    }

    @PatchMapping("/editar/{accountId}")
    public ResponseEntity<?> updateAccount(@PathVariable Long accountId, @RequestBody Double transactionLimit) {
        try {
            return ResponseEntity.ok(accountService.updateAccount(accountId,transactionLimit));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al actualizar cuenta: " + e.getMessage());
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getAccountsByUserId(@PathVariable Long userId) {
        try {
            List<Accounts> accounts = accountService.findAccountsByUserId(userId);
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al encontrar las cuentas del usuario: " + e.getMessage());
        }
    }

    // Endpoint para seleccionar una cuenta y actualizar el token JWT
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
