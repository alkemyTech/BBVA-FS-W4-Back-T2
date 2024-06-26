package AlkemyWallet.AlkemyWallet.unit;

import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.domain.factory.RoleFactory;
import AlkemyWallet.AlkemyWallet.dtos.RegisterResponse;
import AlkemyWallet.AlkemyWallet.dtos.RegisterRequest;
import AlkemyWallet.AlkemyWallet.repositories.UserRepository;
import AlkemyWallet.AlkemyWallet.services.AccountService;
import AlkemyWallet.AlkemyWallet.services.AuthenticationService;
import AlkemyWallet.AlkemyWallet.services.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class RegisterTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    AccountService accountService;

    @Mock
    private RoleFactory roleFactory;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    public void testRegisterSuccess() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUserName("testUser");
        registerRequest.setPassword("password");
        registerRequest.setDni("12345678");
        registerRequest.setBirthDate("10-02-2003");

        User mockUser = new User();
        mockUser.setUserName("testUser");
        mockUser.setId(1L);

        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        when(jwtService.getToken(mockUser.getUsername())).thenReturn("validToken");

        RegisterResponse registerResponse = authenticationService.register(registerRequest);

        assertEquals("testUser", registerResponse.getUserName());
        assertEquals("validToken", jwtService.getToken(registerResponse.getUserName()));
    }

    @Test
    public void testRegisterUserAlreadyExists() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUserName("existingUser");
        registerRequest.setPassword("password");
        registerRequest.setDni("12345678");
        registerRequest.setBirthDate("10-02-2003");

        when(userRepository.findByUserName("existingUser")).thenReturn(Optional.of(new User()));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authenticationService.register(registerRequest);
        });

        assertEquals("User already exists", exception.getMessage());
    }

}