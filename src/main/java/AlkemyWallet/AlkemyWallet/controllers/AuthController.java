package AlkemyWallet.AlkemyWallet.controllers;

import AlkemyWallet.AlkemyWallet.dtos.RegisterResponse;
import AlkemyWallet.AlkemyWallet.dtos.LoginRequestDTO;
import AlkemyWallet.AlkemyWallet.dtos.LoginResponseDTO;
import AlkemyWallet.AlkemyWallet.dtos.RegisterRequest;
import AlkemyWallet.AlkemyWallet.exceptions.UserDeletedException;
import AlkemyWallet.AlkemyWallet.services.AuthenticationService;
import AlkemyWallet.AlkemyWallet.services.JwtService;
import AlkemyWallet.AlkemyWallet.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;


@RestController
@AllArgsConstructor
@RequestMapping("/auth")
@Validated
public class AuthController {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final UserService userService;



    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<String> handleAuthenticationException(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request");
    }

    @ExceptionHandler(RuntimeException.class) //  Añadi un manejador de excepciones RuntimeException
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar la solicitud");
    }
    @ExceptionHandler(UserDeletedException.class)
    public ResponseEntity<String> handleUserDeletedException(UserDeletedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User account is deleted");
    }
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
            LoginResponseDTO user = authenticationService.login(loginRequest);
            String token =jwtService.getToken(user.getUserName());
                    // Línea que lanza UserDeletedException
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);

            return ResponseEntity.ok().headers(headers).body(user);
        } catch (AuthenticationException e) {
            return handleAuthenticationException(e);
        } catch (UserDeletedException e) {
            return handleUserDeletedException(e);
        } catch (IllegalArgumentException e) {
            return handleIllegalArgumentException(e);
        } catch (RuntimeException e) {
            return handleRuntimeException(e);
        }
    }

    @Operation(
            description = "Endpoint accesible para registro de nuevos usuarios",
            summary = "Registra un nuevo usuario",
            responses = {
                    @ApiResponse(
                            description = "Registro exitoso",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = RegisterResponse.class), mediaType = "application/json")
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
        try {
            RegisterResponse registerResponse = authenticationService.register(registerRequest);
            String token = jwtService.getToken(registerResponse.getUserName());
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);

            return ResponseEntity.ok().headers(headers).body(registerResponse);
        } catch (IllegalArgumentException e) {
            return handleIllegalArgumentException(e);
        } catch (RuntimeException e) {
            return handleRuntimeException(e);
        }
    }




    @Operation(
            description = "Endpoint accesible para registro de nuevos administradores",
            summary = "Registra un nuevo administrador",
            responses = {
                    @ApiResponse(
                            description = "Registro exitoso",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = RegisterResponse.class), mediaType = "application/json")
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
        try {
            RegisterResponse registerResponse = authenticationService.registerAdmin(registerRequest);
            String token = jwtService.getToken(registerResponse.getUserName());
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);

            return ResponseEntity.ok().headers(headers).body(registerResponse);
        } catch (IllegalArgumentException e) {
            return handleIllegalArgumentException(e);
        }
    }

    @GetMapping("/validate-token")
    public ResponseEntity<Void> validateToken(HttpServletRequest request) {
        String token = jwtService.getTokenFromRequest(request);


        if (token != null && jwtService.isTokenValid(token)) {
            String username = jwtService.getUsernameFromToken(token);

            UserDetails userDetails = userService.loadUserByUsername(username);

            if (jwtService.isTokenValidForUser(token, userDetails)) {
                // Token y usuario válidos
                HttpHeaders headers = new HttpHeaders();
                headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);


                return ResponseEntity.ok().headers(headers).build();
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}







