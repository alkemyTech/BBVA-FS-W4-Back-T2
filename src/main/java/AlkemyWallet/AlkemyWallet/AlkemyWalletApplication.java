package AlkemyWallet.AlkemyWallet;

import AlkemyWallet.AlkemyWallet.config.CurrencyConfig;
import AlkemyWallet.AlkemyWallet.enums.CurrencyEnum;
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

	public static void main(String[] args) {
		SpringApplication.run(AlkemyWalletApplication.class, args);


		// Conexion base de datos
		String url = "jdbc:mysql://localhost:3306/alkemywallet";
		String user = "root";
		String password = "gatosmonteses";
		try{
			Connection conexion = DriverManager.getConnection(url,user,password);
			System.out.printf("Conexion establecida con exito\n");
			conexion.close();
		} catch (
				SQLException e) {
			System.out.println("Error al conectar con la base de datos");
		}
	}
	@PostConstruct
	public void init() {
		CurrencyEnum.initializeLimits(currencyConfig);
	}

}
