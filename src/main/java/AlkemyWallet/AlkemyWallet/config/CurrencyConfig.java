package AlkemyWallet.AlkemyWallet.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CurrencyConfig {

    @Value("${transaction.limit.ARS}")
    private double arsLimit;

    @Value("${transaction.limit.USD}")
    private double usdLimit;

    public double getArsLimit() {
        return arsLimit;
    }

    public double getUsdLimit() {
        return usdLimit;
    }
}