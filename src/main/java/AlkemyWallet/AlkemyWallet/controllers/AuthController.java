package AlkemyWallet.AlkemyWallet.controllers;


import AlkemyWallet.AlkemyWallet.dtos.LoginRequest;
import AlkemyWallet.AlkemyWallet.dtos.RegisterRequest;
import AlkemyWallet.AlkemyWallet.services.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

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
            return ResponseEntity.ok(authenticationService.login(loginRequest));
        } catch (AuthenticationException e) {
            return handleAuthenticationException(e);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            return ResponseEntity.ok(authenticationService.register(registerRequest));
        } catch (IllegalArgumentException e) {
            return handleIllegalArgumentException(e);
        }
    }
    @PostMapping("/register/admin")
    public ResponseEntity<?> registerAdmin(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            return ResponseEntity.ok(authenticationService.registerAdmin(registerRequest));
        } catch (IllegalArgumentException e) {
            return handleIllegalArgumentException(e);
        }
    }
}


