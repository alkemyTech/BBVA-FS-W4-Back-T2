package com.alkemy.taskmanager.security.core;

import AlkemyWallet.AlkemyWallet.domain.Role;
import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.repositories.UserRepository;
import AlkemyWallet.AlkemyWallet.security.AuthenticationManagerService;
import AlkemyWallet.AlkemyWallet.services.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
@AllArgsConstructor
public class AuthenticationService {

    private final AuthenticationManagerService authenticationManager;
    private final JwtService jwtService;
    private final UserRepository taskUserRepository;
    private final PasswordEncoder passwordEncoder;

    public String login(String email, String password) throws AuthenticationException {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        var user = (User) authentication.getPrincipal();
        return jwtService.createToken(user.getEmail(), 60);
    }


    //Registro de usuario
    /*
    public User registerUser(String email, String password){
        return register(email, password, Role.USER);
    }

    /*
    public UserDetails loadUserByEmail(String username) {
        return UserRepository.findByEmail(username).orElseThrow();
    }


    /*
    private User register(String email, String password, Role role) {
        if (taskUserRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }

        User newUser = new User(email, passwordEncoder.encode(password), role);
        return taskUserRepository.save(newUser);
    }
    */
}