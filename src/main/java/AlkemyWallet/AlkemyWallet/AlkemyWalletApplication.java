package AlkemyWallet.AlkemyWallet;

import AlkemyWallet.AlkemyWallet.config.CurrencyConfig;
import AlkemyWallet.AlkemyWallet.enums.CurrencyEnum;
import AlkemyWallet.AlkemyWallet.repositories.seeders.AccountsSeeder;
import AlkemyWallet.AlkemyWallet.services.DatabaseService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@SpringBootApplication
public class AlkemyWalletApplication {
	@Autowired
	private CurrencyConfig currencyConfig;
	@Autowired
	private DatabaseService databaseService;
    @Autowired
    private AccountsSeeder accountsSeeder;

	public static void main(String[] args) {
		SpringApplication.run(AlkemyWalletApplication.class, args);
	}
	@PostConstruct
	public void init() {
		databaseService.checkDatabaseConnection();
		CurrencyEnum.initializeLimits(currencyConfig);
		//Seed Account
		var AccountsSeeder = new AccountsSeeder();
		accountsSeeder.seedAccounts();
	}


}
