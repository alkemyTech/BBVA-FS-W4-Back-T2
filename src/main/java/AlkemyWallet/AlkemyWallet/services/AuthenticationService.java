package AlkemyWallet.AlkemyWallet.services;

import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.domain.factory.RoleFactory;
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

import java.time.LocalDateTime;


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

        roleFactory.initializeRoles();
        User user = User.builder()
                .userName(registerRequest.getUserName())
                .password(passwordEncoder.encode( registerRequest.getPassword()))
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .birthDate(registerRequest.getBirthDate())
                .role(RoleFactory.getUserRole())
                .creationDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .softDelete(false)
                .imagePath("")
                .build();
        if (userRepository.findByUserName(registerRequest.getUserName()).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }

        userRepository.save(user);

        //Acá añadir cuentas

        //Cuenta USD
        accountService.addById(CurrencyEnum.USD,user.getId());
        //Cuenta ARG
        accountService.addById(CurrencyEnum.ARS,user.getId());


        return AuthResponseRegister.builder()
                .userName(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    public AuthResponseRegister registerAdmin(RegisterRequest registerRequest) {

        roleFactory.initializeRoles();
        User user = User.builder()
                .userName(registerRequest.getUserName())
                .password(passwordEncoder.encode( registerRequest.getPassword()))
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .birthDate(registerRequest.getBirthDate())
                .role(RoleFactory.getAdminRole())
                .creationDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .softDelete(false)
                .build();
        if (userRepository.findByUserName(registerRequest.getUserName()).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }

        userRepository.save(user);

        //Acá añadir cuentas

        //Cuenta USD
        accountService.addById(CurrencyEnum.USD,user.getId());
        //Cuenta ARG
        accountService.addById(CurrencyEnum.ARS,user.getId());


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
        System.out.println("Usuario : " + user.getUsername());

        //EN DUDA SI NO CAMBIARLO POR UNA AUTHRESPONSELOGIN - RESPONDER DIRECTO EL TOKEN O DEJARLO CON TOKEN Y USERDEATLLES
        return jwtService.getToken(user.getUsername());
    }






}