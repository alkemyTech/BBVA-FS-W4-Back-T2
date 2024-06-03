package AlkemyWallet.AlkemyWallet.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@PropertySource("classpath:loan.properties")
public class LoanConfig {

    @Value("${interest.monthly.percentage}")
    private Double interestMonthlyPercentage;

}