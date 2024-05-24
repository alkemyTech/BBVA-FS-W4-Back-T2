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

    private final UserService userService; // Un servicio para interactuar con los usuarios en tu sistema
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();

        // Verificar si el correo electrónico existe y obtener la información del usuario
        User user = (User) userService.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Correo electrónico no encontrado: " + email);
        }

        // Verificar si la contraseña proporcionada coincide con la contraseña almacenada
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Contraseña incorrecta");
        }

        // Crear y devolver una instancia de Authentication para el usuario autenticado
        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }
}