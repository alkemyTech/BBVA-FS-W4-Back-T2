package AlkemyWallet.AlkemyWallet;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import AlkemyWallet.AlkemyWallet.domain.Role;
import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.domain.factory.RoleFactory;
import AlkemyWallet.AlkemyWallet.dtos.AccountRequestDto;
import AlkemyWallet.AlkemyWallet.dtos.AuthResponseRegister;
import AlkemyWallet.AlkemyWallet.dtos.RegisterRequest;
import AlkemyWallet.AlkemyWallet.enums.RoleEnum;
import AlkemyWallet.AlkemyWallet.repositories.RoleRepository;
import AlkemyWallet.AlkemyWallet.repositories.UserRepository;
import AlkemyWallet.AlkemyWallet.services.AccountService;
import AlkemyWallet.AlkemyWallet.services.AuthenticationService;
import AlkemyWallet.AlkemyWallet.services.JwtService;
import AlkemyWallet.AlkemyWallet.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AccountService accountService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserService userService;

    @InjectMocks
    private AuthenticationService authenticationService;

    private RoleFactory roleFactory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        roleFactory = new RoleFactory(roleRepository);
        roleFactory.initializeRoles();
        authenticationService = new AuthenticationService(authenticationManager, jwtService, userRepository, passwordEncoder, roleFactory, accountService);
    }

    @Test
    void testCreateUserWithMandatoryFields() {
        RegisterRequest registerRequest = new RegisterRequest("john.doe@example.com","password" , "John", "Doe", "01-01-2000");
        when(userRepository.findByUserName(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        AuthResponseRegister response = authenticationService.register(registerRequest);

        assertNotNull(response);
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertEquals("john.doe@example.com", response.getUserName());
        verify(userRepository, times(1)).save(any(User.class));
        // Capturar el ID del usuario simulado
        ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
        verify(accountService, times(2)).addById(any(AccountRequestDto.class), userIdCaptor.capture());

    }

    @Test
    void testEmailUniqueness() {
        RegisterRequest registerRequest = new RegisterRequest("john.doe@example.com","password" , "John", "Doe", "01-01-2000");
        when(userRepository.findByUserName(anyString())).thenReturn(Optional.of(new User()));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            authenticationService.register(registerRequest);
        });

        assertEquals("User already exists", exception.getMessage());
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    void testTimestampsGeneration() {
        RegisterRequest registerRequest = new RegisterRequest("John", "Doe", "john.doe@example.com", "password", "01-01-2000");
        when(userRepository.findByUserName(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        AuthResponseRegister response = authenticationService.register(registerRequest);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertNotNull(savedUser.getCreationDate());
        assertNotNull(savedUser.getUpdateDate());
    }

    @Test
    void testSoftDeleteUser() {
        User user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .userName("john.doe@example.com")
                .password("encodedPassword")
                .creationDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .softDelete(0)
                .birthDate(LocalDate.of(2000, 1, 1))
                .role(new Role(1L, RoleEnum.USER, "User role", LocalDateTime.now(), LocalDateTime.now()))
                .build();

       when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        // Marcar el usuario como baja
        userService.softDeleteById(user.getId());
        assertFalse(user.isEnabled());
        verify(userRepository).save(user);

        //Simular que el usuario fue guardado y devuelto con softDelete = 1
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        //Marcar el usuario como alta
        userService.softDeleteById(user.getId());
        assertFalse(user.isSoftDelete());
        verify(userRepository, times(2)).save(user);
    }


}
