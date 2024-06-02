package AlkemyWallet.AlkemyWallet.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class CurrencyConfig {

    @Value("${transaction.limit.ARS}")
    private double arsLimit;

    @Value("${transaction.limit.USD}")
    private double usdLimit;

}