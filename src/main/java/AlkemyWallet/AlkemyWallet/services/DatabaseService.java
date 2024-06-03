package AlkemyWallet.AlkemyWallet.services;

import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Service
public class DatabaseService {

    private final String url = "jdbc:mysql://localhost:3306/alkemywallet";
    private final String user = "root";
    private final String password = "gatosmonteses";

    public void checkDatabaseConnection() {
        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            System.out.println("Conexión establecida con éxito");
            connection.close();
        } catch (SQLException e) {
            System.out.println("Error al conectar con la base de datos: " + e.getMessage());
        }
    }
}
