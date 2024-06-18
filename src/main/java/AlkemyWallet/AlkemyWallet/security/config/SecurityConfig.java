package AlkemyWallet.AlkemyWallet.security.config;

import AlkemyWallet.AlkemyWallet.security.JwtAuthenticationFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;



@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    //IR CAMBIANDO LOS .REQUESTMATCHERS ("/.../").*los persmisos*
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authRequest ->
                        authRequest
                                .requestMatchers("/auth/register/admin").hasRole("ADMIN")
                                .requestMatchers("/auth/**").permitAll()

                                //rutas de Accounts
                                .requestMatchers("/accounts/balance").permitAll()
                                .requestMatchers("/accounts/create").permitAll()
                                .requestMatchers("/accounts/select/**").permitAll()
                                .requestMatchers("/accounts/editar/**").permitAll()
                                .requestMatchers("/accounts/myAccounts/**").permitAll()
                                .requestMatchers("/accounts/{userId}").hasRole("ADMIN")
                                .requestMatchers("/accounts").hasRole("ADMIN")

                                .requestMatchers("/fixedTerm/**").permitAll()
                                .requestMatchers("/loan/**").permitAll()

                                //rutas de Usuarios
                                .requestMatchers("/id/{id}").hasRole("ADMIN")
                                .requestMatchers("/users").hasRole("ADMIN")
                                .requestMatchers("/**").permitAll()
                                .requestMatchers("/cbu/{idCbu}/users/{idUser}").permitAll()

                                //rutas de transacciones
                                .requestMatchers("/transactions/user/{userId}").hasRole("ADMIN")
                                .requestMatchers("/accounts/**").permitAll()
                                .requestMatchers("/transactions/**").permitAll()
                                .requestMatchers("/register/admin").hasRole("ADMIN")
                                .requestMatchers("/loan/**").permitAll()
                                .requestMatchers("/v3/api-docs/**","/swagger-ui/**","/swagger-ui.html").permitAll()
                                .anyRequest().authenticated()
                )
                .sessionManagement(sessionManager->
                        sessionManager
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();


    }
}
