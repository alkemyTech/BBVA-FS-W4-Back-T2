package AlkemyWallet.AlkemyWallet.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class FixedTermDepositConfig {
    @Value("${fixed.term.interest}")
    private double fixedTermInterest;
}
