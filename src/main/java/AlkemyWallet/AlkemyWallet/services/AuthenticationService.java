package AlkemyWallet.AlkemyWallet.services;

import AlkemyWallet.AlkemyWallet.domain.Role;
import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.domain.factory.RoleFactory;
import AlkemyWallet.AlkemyWallet.dtos.AccountRequestDto;
import AlkemyWallet.AlkemyWallet.dtos.AuthResponseRegister;
import AlkemyWallet.AlkemyWallet.dtos.LoginRequestDTO;
import AlkemyWallet.AlkemyWallet.dtos.RegisterRequest;
import AlkemyWallet.AlkemyWallet.exceptions.UserDeletedException;
import AlkemyWallet.AlkemyWallet.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Service
@AllArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleFactory roleFactory;
    private final AccountService accountService;


    public AuthResponseRegister register(RegisterRequest registerRequest) {
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

    public AuthResponseRegister registerAdmin(RegisterRequest registerRequest) {
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

    private AuthResponseRegister buildAuthResponseRegister(User user) {
        return AuthResponseRegister.builder()
                .userName(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }



    public String login(LoginRequestDTO loginRequest) throws UserDeletedException {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassword()));

        UserDetails user = userRepository.findByUserName(loginRequest.getUserName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (!user.isEnabled()) {
            throw new UserDeletedException("No se puede iniciar sesión, el usuario está marcado como borrado");
        }


        //EN DUDA SI NO CAMBIARLO POR UNA AUTHRESPONSELOGIN - RESPONDER DIRECTO EL TOKEN O DEJARLO CON TOKEN Y USERDEATLLES
        return jwtService.getToken(user.getUsername());
    }






}