package AlkemyWallet.AlkemyWallet.config;

import AlkemyWallet.AlkemyWallet.repositories.seeders.UserSeeder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {

    @Autowired
    private UserSeeder userSeeder;

    @Bean
    public CommandLineRunner runSeeder(){
        return args -> {
            userSeeder.seed();
        };
    }
}