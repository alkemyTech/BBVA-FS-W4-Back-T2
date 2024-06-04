package AlkemyWallet.AlkemyWallet.config;

import AlkemyWallet.AlkemyWallet.repositories.seeders.UserSeeder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {

    @Bean
    public CommandLineRunner runSeeder(UserSeeder userSeeder) {
        return args -> {
            userSeeder.seed();
        };
    }
}