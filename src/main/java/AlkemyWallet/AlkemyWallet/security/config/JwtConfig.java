package AlkemyWallet.AlkemyWallet.security.config;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtConfig {
    @NotNull
    private String secret;
    @NotNull
    private String issuer;
}