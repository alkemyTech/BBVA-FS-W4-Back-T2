package AlkemyWallet.AlkemyWallet.unit;

import AlkemyWallet.AlkemyWallet.controllers.AuthController;
import AlkemyWallet.AlkemyWallet.dtos.AuthResponseRegister;
import AlkemyWallet.AlkemyWallet.dtos.RegisterRequest;
import AlkemyWallet.AlkemyWallet.services.AuthenticationService;
import AlkemyWallet.AlkemyWallet.services.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RegisterTest {

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

    @Test //espera un 200 y le estoy pasando un 500
    public void testRegisterSuccess() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUserName("testUser");
        registerRequest.setPassword("password");

        AuthResponseRegister authResponseRegister = new AuthResponseRegister();
        authResponseRegister.setUserName("testUser");

        when(authenticationService.register(registerRequest)).thenReturn(authResponseRegister);
        when(jwtService.getToken(authResponseRegister.getUserName())).thenReturn("validToken");

        ResponseEntity<?> response = authController.register(registerRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(authResponseRegister, response.getBody());
        assertEquals("Bearer validToken", response.getHeaders().getFirst("Authorization"));
    }

    @Test // espera un 400
    public void testRegisterUserAlreadyExists() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUserName("existingUser");
        registerRequest.setPassword("password");

        when(authenticationService.register(registerRequest)).thenThrow(new IllegalArgumentException("User already exists"));

        ResponseEntity<?> response = authController.register(registerRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User already exists", response.getBody());
    }

    @Test //espera un 400
    public void testRegisterInvalidRequest() throws NoSuchMethodException {
        // Configurar una solicitud de registro inválida
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUserName("");
        registerRequest.setPassword("password");

        // Configurar el comportamiento esperado para la validación de argumentos
        Method method = AuthController.class.getMethod("register", RegisterRequest.class);
        MethodParameter methodParameter = new MethodParameter(method, 0);
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        // Lanzar una excepción no verificada en lugar de una verificada
        RuntimeException exception = new RuntimeException("La validación falló");

        // Configurar el comportamiento esperado para el servicio de autenticación
        when(authenticationService.register(registerRequest)).thenThrow(exception);

        // Llamar al método register del controlador y capturar la respuesta
        ResponseEntity<?> response = authController.register(registerRequest);

        // Verificar que se reciba una respuesta de error con el código de estado y el mensaje adecuados
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Solicitud inválida", response.getBody());
    }


    @Test
    public void testRegisterInternalServerError() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUserName("testUser");
        registerRequest.setPassword("password");

        // Lanzar RuntimeException para simular un error del servidor
        when(authenticationService.register(registerRequest)).thenThrow(new RuntimeException("Internal Server Error"));

        ResponseEntity<?> response = authController.register(registerRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error al procesar la solicitud", response.getBody());
    }
}





