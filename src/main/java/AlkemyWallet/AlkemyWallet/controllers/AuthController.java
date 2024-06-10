package AlkemyWallet.AlkemyWallet.controllers;

import AlkemyWallet.AlkemyWallet.dtos.AuthResponseRegister;
import AlkemyWallet.AlkemyWallet.dtos.LoginRequestDTO;
import AlkemyWallet.AlkemyWallet.dtos.RegisterRequest;
import AlkemyWallet.AlkemyWallet.exceptions.UserDeletedException;
import AlkemyWallet.AlkemyWallet.services.AuthenticationService;
import AlkemyWallet.AlkemyWallet.services.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;



    @Operation(
            description = "Endpoint accesible para autenticación de usuarios",
            summary = "Inicia sesión con credenciales de usuario",
            responses = {
                    @ApiResponse(
                            description = "Login successful!",
                            responseCode = "200",
                            content = @Content(mediaType = "text/plain")
                    ),
                    @ApiResponse(
                            description = "Invalid username or password",
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
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequest, HttpServletResponse response) {
        try {
            // Para usar header
            String token = authenticationService.login(loginRequest);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);

            return ResponseEntity.ok().headers(headers).body("Login successful!");
        } catch (UserDeletedException e) {
            throw new RuntimeException(e);
        }
    }

    @Operation(
            description = "Endpoint accesible para registro de nuevos usuarios",
            summary = "Registra un nuevo usuario",
            responses = {
                    @ApiResponse(
                            description = "Registro exitoso",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = AuthResponseRegister.class), mediaType = "application/json")
                    ),
                    @ApiResponse(
                            description = "Solicitud inválida",
                            responseCode = "400",
                            content = @Content(mediaType = "text/plain")
                    ),
                    @ApiResponse(
                            description = "Error al procesar la solicitud",
                            responseCode = "500",
                            content = @Content(mediaType = "text/plain")
                    )
            }
    )
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
            AuthResponseRegister registerResponse = authenticationService.register(registerRequest);
            String token = jwtService.getToken(registerResponse.getUserName());
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);

            return ResponseEntity.ok().headers(headers).body(registerResponse);
    }

    @Operation(
            description = "Endpoint accesible para registro de nuevos administradores",
            summary = "Registra un nuevo administrador",
            responses = {
                    @ApiResponse(
                            description = "Registro exitoso",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = AuthResponseRegister.class), mediaType = "application/json")
                    ),
                    @ApiResponse(
                            description = "Solicitud inválida",
                            responseCode = "400",
                            content = @Content(mediaType = "text/plain")
                    ),
                    @ApiResponse(
                            description = "Error al procesar la solicitud",
                            responseCode = "500",
                            content = @Content(mediaType = "text/plain")
                    )
            }
    )
    @PostMapping("/register/admin")
    public ResponseEntity<?> registerAdmin(@Valid @RequestBody RegisterRequest registerRequest) {

            AuthResponseRegister registerResponse = authenticationService.registerAdmin(registerRequest);
            String token = jwtService.getToken(registerResponse.getUserName());
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);

            return ResponseEntity.ok().headers(headers).body(registerResponse);

    }
}
