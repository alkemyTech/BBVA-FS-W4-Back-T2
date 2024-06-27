package AlkemyWallet.AlkemyWallet.config;

import AlkemyWallet.AlkemyWallet.repositories.seeders.AccountsSeeder;
import AlkemyWallet.AlkemyWallet.repositories.seeders.TransactionSeeder;
import AlkemyWallet.AlkemyWallet.repositories.seeders.UserSeeder;
import AlkemyWallet.AlkemyWallet.repositories.seeders.TransactionSeeder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {

    @Autowired
    private UserSeeder userSeeder;

    @Autowired
    private AccountsSeeder accountsSeeder;

    @Autowired
    private TransactionSeeder transactionSeeder;

    //@Bean
    //public CommandLineRunner runSeeder() {
    //    return args -> {
           // userSeeder.seed();
            //accountsSeeder.seedAccounts();
            //transactionSeeder.seedTransactions();
    //    };

   // }
}
