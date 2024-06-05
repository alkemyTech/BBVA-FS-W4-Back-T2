package AlkemyWallet.AlkemyWallet;

import AlkemyWallet.AlkemyWallet.config.AppConfiguration;
import AlkemyWallet.AlkemyWallet.config.CurrencyConfig;
import AlkemyWallet.AlkemyWallet.domain.factory.RoleFactory;
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

	public static void main(String[] args) {
		SpringApplication.run(AlkemyWalletApplication.class, args);
	}
	@PostConstruct
	public void init() {
		databaseService.checkDatabaseConnection();
		CurrencyEnum.initializeLimits(currencyConfig);
	}


}
