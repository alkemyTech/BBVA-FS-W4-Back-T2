package AlkemyWallet.AlkemyWallet.controllers;


import AlkemyWallet.AlkemyWallet.dtos.AuthResponseRegister;
import AlkemyWallet.AlkemyWallet.dtos.LoginRequest;
import AlkemyWallet.AlkemyWallet.dtos.RegisterRequest;
import AlkemyWallet.AlkemyWallet.services.AuthenticationService;
import AlkemyWallet.AlkemyWallet.services.JwtService;
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

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<String> handleAuthenticationException(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        try {
            // Para usar header
            String token = authenticationService.login(loginRequest);
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);

            return ResponseEntity.ok().headers(headers).body("Login successful!");
        } catch (AuthenticationException e) {
            return handleAuthenticationException(e);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            AuthResponseRegister registerResponse = authenticationService.register(registerRequest);
            String token = jwtService.getToken(registerResponse.getUserName());
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);


            return ResponseEntity.ok().headers(headers).body(registerResponse);
        } catch (IllegalArgumentException e) {
            return handleIllegalArgumentException(e);
        }
    }
    @PostMapping("/register/admin")
    public ResponseEntity<?> registerAdmin(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            AuthResponseRegister registerResponse = authenticationService.registerAdmin(registerRequest);
            String token = jwtService.getToken(registerResponse.getUserName());
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);


            return ResponseEntity.ok().headers(headers).body(registerResponse);
        } catch (IllegalArgumentException e) {
            return handleIllegalArgumentException(e);
        }
    }
}


