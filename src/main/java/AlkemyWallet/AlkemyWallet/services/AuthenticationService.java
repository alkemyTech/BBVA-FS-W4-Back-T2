package AlkemyWallet.AlkemyWallet.services;

import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.domain.factory.RoleFactory;
import AlkemyWallet.AlkemyWallet.dtos.AccountRequestDto;
import AlkemyWallet.AlkemyWallet.dtos.AuthResponseRegister;
import AlkemyWallet.AlkemyWallet.dtos.LoginRequest;
import AlkemyWallet.AlkemyWallet.dtos.RegisterRequest;
import AlkemyWallet.AlkemyWallet.enums.CurrencyEnum;
import AlkemyWallet.AlkemyWallet.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
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

        //Lógica para pasar String de fecha de nacimiento a LocalDate
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate birthDate = LocalDate.parse(registerRequest.getBirthDate(), formatter);

        roleFactory.initializeRoles();
        User user = User.builder()
                .userName(registerRequest.getUserName())
                .password(passwordEncoder.encode( registerRequest.getPassword()))
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .birthDate(birthDate)
                .role(RoleFactory.getUserRole())
                .creationDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();
        if (userRepository.findByUserName(registerRequest.getUserName()).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }

        userRepository.save(user);

        //Acá añadir cuentas

        //Acá añadir cuentas
        AccountRequestDto accountArs = new AccountRequestDto("ARS","CAJA_AHORRO");
        AccountRequestDto accountUsd = new AccountRequestDto("USD","CAJA_AHORRO");

//        //Cuenta ARS
        accountService.addById(accountArs,user.getId());
//        //Cuenta USD
        accountService.addById(accountUsd,user.getId());


        return AuthResponseRegister.builder()
                .userName(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    public AuthResponseRegister registerAdmin(RegisterRequest registerRequest) {

        //Lógica para pasar String de fecha de nacimiento a LocalDate
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate birthDate = LocalDate.parse(registerRequest.getBirthDate(), formatter);

        roleFactory.initializeRoles();
        User user = User.builder()
                .userName(registerRequest.getUserName())
                .password(passwordEncoder.encode( registerRequest.getPassword()))
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .birthDate(birthDate)
                .role(RoleFactory.getAdminRole())
                .creationDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .softDelete(0)
                .build();
        if (userRepository.findByUserName(registerRequest.getUserName()).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }

        userRepository.save(user);

        return AuthResponseRegister.builder()
                .userName(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }


    public String login(LoginRequest loginRequest) throws AuthenticationException {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassword()));

        UserDetails user = userRepository.findByUserName(loginRequest.getUserName())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        //EN DUDA SI NO CAMBIARLO POR UNA AUTHRESPONSELOGIN - RESPONDER DIRECTO EL TOKEN O DEJARLO CON TOKEN Y USERDEATLLES
        return jwtService.getToken(user.getUsername());
    }






}