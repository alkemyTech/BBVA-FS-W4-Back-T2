package AlkemyWallet.AlkemyWallet.services;

import AlkemyWallet.AlkemyWallet.domain.Role;
import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.domain.factory.RoleFactory;
import AlkemyWallet.AlkemyWallet.dtos.*;
import AlkemyWallet.AlkemyWallet.exceptions.UnauthorizedUserException;
import AlkemyWallet.AlkemyWallet.exceptions.UserDeletedException;
import AlkemyWallet.AlkemyWallet.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.regex.Pattern;


@Service
@AllArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleFactory roleFactory;
    private final AccountService accountService;


    public RegisterResponse register(RegisterRequest registerRequest) {

        if (!isValidEmailAddress(registerRequest.getUserName())) {
            throw new IllegalArgumentException("Invalid email address format");
        }

        if (userExists(registerRequest.getUserName())) {
            throw new IllegalArgumentException("User already exists");
        }

        LocalDate birthDate = parseBirthDate(registerRequest.getBirthDate());
        roleFactory.initializeRoles();
        User user = createUser(registerRequest, RoleFactory.getUserRole(), birthDate);
        user.setSoftDelete(false);

        saveUser(user);
        createAccounts(user.getId());

        return buildAuthResponseRegister(user);
    }

    // Método auxiliar para validar el formato de correo electrónico
    private boolean isValidEmailAddress(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return Pattern.compile(regex).matcher(email).matches();
    }

    public RegisterResponse registerAdmin(RegisterRequest registerRequest) {
        LocalDate birthDate = parseBirthDate(registerRequest.getBirthDate());
        roleFactory.initializeRoles();
        User user = createUser(registerRequest, RoleFactory.getAdminRole(), birthDate);
        user.setSoftDelete(false);

        if (userExists(registerRequest.getUserName())) {
            throw new IllegalArgumentException("User already exists");
        }

        saveUser(user);

        return buildAuthResponseRegister(user);
    }

    private LocalDate parseBirthDate(String birthDateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return LocalDate.parse(birthDateString, formatter);
    }

    private User createUser(RegisterRequest registerRequest, Role role, LocalDate birthDate) {

        return User.builder()
                .userName(registerRequest.getUserName())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .birthDate(birthDate)
                .role(role)
                .creationDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .dni(registerRequest.getDni())
                .build();
    }

    private boolean userExists(String userName) {
        return userRepository.findByUserName(userName).isPresent();
    }

    private void saveUser(User user) {
        userRepository.save(user);
    }

    private void createAccounts(Long userId) {
        AccountRequestDto accountArs = new AccountRequestDto("ARS", "CAJA_AHORRO");
        AccountRequestDto accountUsd = new AccountRequestDto("USD", "CAJA_AHORRO");

        accountService.addById(accountArs, userId);
        accountService.addById(accountUsd, userId);
    }

    private RegisterResponse buildAuthResponseRegister(User user) {
        return RegisterResponse.builder()
                .userName(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .birthDay(String.valueOf(user.getBirthDate()))
                .imagePath(user.getImagePath())
                .id(user.getId())
                .build();
    }



    public LoginResponseDTO login(LoginRequestDTO loginRequest) throws UserDeletedException {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassword()));

        User user = userRepository.findByUserName(loginRequest.getUserName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (!user.isEnabled()) {
            throw new UserDeletedException("No se puede iniciar sesión, el usuario está marcado como borrado");
        }

        if(!Objects.equals(loginRequest.getDni(), user.getDni())){
            throw new UnauthorizedUserException("DNI Incorrecto");
        }

        return new LoginResponseDTO(user.getId(), user.getUsername(),user.getFirstName(),user.getLastName(),user.getImagePath(),user.getDni(),user.getBirthDate());
    }







}