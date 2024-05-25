package AlkemyWallet.AlkemyWallet.security;

import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AuthenticationManagerService implements AuthenticationManager {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName(); //dice getName pero lo cambiamos en el filtro jwt y deberia tomar el email del jason
        String password = authentication.getCredentials().toString();


        User user = (User) userService.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Correo electrónico no encontrado: " + email);
        }


        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Contraseña incorrecta");
        }


        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }


}