package AlkemyWallet.AlkemyWallet.security.config;
import AlkemyWallet.AlkemyWallet.repositories.seeders.TransactionSeeder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {

    @Autowired
    private TransactionSeeder transactionSeeder;

    @Bean
    public CommandLineRunner runSeeder(){
        return args -> {
            transactionSeeder.run();
        };
    }
}
