package AlkemyWallet.AlkemyWallet.unit;

import AlkemyWallet.AlkemyWallet.controllers.AuthController;
import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.dtos.LoginRequestDTO;
import AlkemyWallet.AlkemyWallet.dtos.LoginResponseDTO;
import AlkemyWallet.AlkemyWallet.exceptions.UserDeletedException;
import AlkemyWallet.AlkemyWallet.repositories.UserRepository;
import AlkemyWallet.AlkemyWallet.services.AuthenticationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class LoginTest {


    @Mock
    UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;


    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void testLoginSuccess() throws UserDeletedException {
        // Mock input data
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUserName("testUser@example.com");
        loginRequest.setPassword("password");
        loginRequest.setDni("12345678");

        // Mocking authentication result
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                loginRequest.getUserName(), loginRequest.getPassword());
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUserName("testUser@example.com");
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        mockUser.setImagePath("path/to/image");
        mockUser.setSoftDelete(false);

        doReturn(new UsernamePasswordAuthenticationToken(mockUser.getUsername(), mockUser.getPassword()))
                .when(authenticationManager).authenticate(authRequest);

        when(userRepository.findByUserName(any(String.class))).thenReturn(Optional.of(mockUser));

        // Perform the login operation
        LoginResponseDTO response = authenticationService.login(loginRequest);

        // Verify the response
        assertEquals(mockUser.getId(), response.getId());
        assertEquals(mockUser.getUsername(), response.getUserName());
        assertEquals(mockUser.getFirstName(), response.getFirstName());
        assertEquals(mockUser.getLastName(), response.getLastName());
        assertEquals(mockUser.getImagePath(), response.getImagePath());
    }


    @Test
    public void testLoginInvalidCredentials() throws UserDeletedException {

        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUserName("wrongUser");
        loginRequest.setPassword("wrongPassword");

        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                loginRequest.getUserName(), loginRequest.getPassword());

        doThrow(new AuthenticationException("Invalid username or password") {})
                .when(authenticationManager).authenticate(authRequest);

        Exception exception = assertThrows(AuthenticationException.class, () -> {
            authenticationService.login(loginRequest);
        });

        assertEquals("Invalid username or password", exception.getMessage());
    }

    @Test
    public void testLoginUserDeleted() throws UserDeletedException {
        // Mock input data
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUserName("deletedUser");
        loginRequest.setPassword("password");

        // Mocking the repository to return a deleted user
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUserName("deletedUser");
        mockUser.setSoftDelete(true); // User is marked as deleted

        when(userRepository.findByUserName(loginRequest.getUserName())).thenReturn(Optional.of(mockUser));

        // Mocking the authentication manager to authenticate the user
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                loginRequest.getUserName(), loginRequest.getPassword());

        doReturn(new UsernamePasswordAuthenticationToken(mockUser.getUsername(), mockUser.getPassword()))
                .when(authenticationManager).authenticate(authRequest);

        // Perform the login operation and assert that the exception is thrown
        UserDeletedException exception = assertThrows(UserDeletedException.class, () -> {
            authenticationService.login(loginRequest);
        });

        // Verify the exception message
        assertEquals("No se puede iniciar sesión, el usuario está marcado como borrado", exception.getMessage());
    }

    @Test
    public void testLoginInvalidRequest() throws NoSuchMethodException, UserDeletedException {
        // Mock input data for invalid request
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUserName("");
        loginRequest.setPassword("password");


        // Perform the login operation and assert that the exception is thrown
        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            authenticationService.login(loginRequest);
        });

        // Verify the exception message
        assertEquals("Usuario no encontrado", thrownException.getMessage());
    }

}

