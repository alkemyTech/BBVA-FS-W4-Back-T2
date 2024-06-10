package AlkemyWallet.AlkemyWallet;

import AlkemyWallet.AlkemyWallet.controllers.AuthController;
import AlkemyWallet.AlkemyWallet.dtos.LoginRequestDTO;
import AlkemyWallet.AlkemyWallet.exceptions.UserDeletedException;
import AlkemyWallet.AlkemyWallet.services.AuthenticationService;
import AlkemyWallet.AlkemyWallet.services.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoginTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLoginSuccess() throws UserDeletedException {
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUserName("testUser");
        loginRequest.setPassword("password");

        when(authenticationService.login(loginRequest)).thenReturn("validToken");

        ResponseEntity<?> response = authController.login(loginRequest, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Login successful!", response.getBody());
        assertEquals("Bearer validToken", response.getHeaders().getFirst("Authorization"));
    }


    @Test
    public void testLoginInvalidCredentials() throws UserDeletedException {
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUserName("wrongUser");
        loginRequest.setPassword("wrongPassword");

        when(authenticationService.login(loginRequest)).thenThrow(new AuthenticationException("Invalid username or password") {
        });

        ResponseEntity<?> response = authController.login(loginRequest, null);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid username or password", response.getBody());
    }

    @Test
    public void testLoginUserDeleted() throws UserDeletedException {
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUserName("deletedUser");
        loginRequest.setPassword("password");

        when(authenticationService.login(loginRequest)).thenThrow(new UserDeletedException("User account is deleted"));

        ResponseEntity<?> response = authController.login(loginRequest, null);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("User account is deleted", response.getBody());
    }

    @Test
    public void testLoginInvalidRequest() throws NoSuchMethodException, UserDeletedException {
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUserName("");
        loginRequest.setPassword("password");

        Method method = AuthController.class.getMethod("login", LoginRequestDTO.class, HttpServletResponse.class);
        MethodParameter methodParameter = new MethodParameter(method, 0);
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        RuntimeException exception = new RuntimeException("La validación falló");
        when(authenticationService.login(loginRequest)).thenThrow(exception);

        ResponseEntity<?> response = authController.login(loginRequest, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid request", response.getBody());
    }

    @Test
    public void testLoginInternalServerError() throws UserDeletedException {
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUserName("testUser");
        loginRequest.setPassword("password");

        when(authenticationService.login(loginRequest)).thenThrow(new RuntimeException("Internal Server Error"));

        ResponseEntity<?> response = authController.login(loginRequest, null);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error al procesar la solicitud", response.getBody());
    }

}
