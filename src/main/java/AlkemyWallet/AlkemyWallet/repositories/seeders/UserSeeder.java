package AlkemyWallet.AlkemyWallet.repositories.seeders;
import AlkemyWallet.AlkemyWallet.domain.User;

import AlkemyWallet.AlkemyWallet.domain.factory.RoleFactory;
import AlkemyWallet.AlkemyWallet.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class UserSeeder {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleFactory roleFactory;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public void seed() {

        roleFactory.initializeRoles();
        // Crear usuarios administradores y regulares utilizando los nombres predefinidos
        for (int i = 0; i < 10; i++) {

            createAdminUser(); // Rol de administrador
            createRegularUser(); // Rol regular
        }
    }
    private int indexNames = 0;
    private int indexLastNames = 0;
    private  int indexEmailUser = 0;
    private int indexEmailAdmin = 0;

    private final List<String> predefinedNames = List.of("Juan", "María", "Frank", "Luk", "Ricardo","Moria", "Mirtha", "Duki", "Lionel", "Lizy");
    private final List<String> predefinedLastNames =List.of("Pecados","Becerra", "Sinatra", "Ra","Fort", "Casan", "Legrand","Lombardo", "Messi", "Tagliani" );
    private final List<String> predefinedEmails = List.of("juan@example.com", "maria@example.com", "frank@example.com", "luk@example.com", "ricardo@example.com", "moria@example.com", "mirtha@example.com", "duki@example.com", "messi@example.com", "lizy@example.com");
    private final List<String> predefinedEmailsAdmin = List.of("juanAdmin@example.com", "mariaAdmin@example.com", "frankAdmin@example.com", "lukAdmin@exampleA.com", "ricardoAdmin@example.com", "moriaAdmin@example.com", "mirthaAdmin@example.com", "dukiAdmin@example.com", "messiAdmin@example.com", "lizyAdmin@example.com");

    private void createAdminUser() {

        User user = User.builder()
                .userName(getNextPredefinedEmailAdmin())
                .password(passwordEncoder.encode("adminPassword"))
                .firstName(getNextPredefinedName())
                .lastName(getNextPredefinedLastName())
                .birthDate(generateRandomBirthDate())
                .role(RoleFactory.getAdminRole())
                .creationDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .softDelete(0)
                .build();
        userRepository.save(user);
    }


    private void createRegularUser() {

        User user = User.builder()
                .userName(getNextPredefinedEmail())
                .password(passwordEncoder.encode("password"))
                .firstName(getNextPredefinedName())
                .lastName(getNextPredefinedLastName())
                .birthDate(generateRandomBirthDate())
                .role(RoleFactory.getUserRole())
                .creationDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .softDelete(0)
                .build();
        userRepository.save(user);
    }

    private String getNextPredefinedName() {
        // Obtener el siguiente nombre de la lista predefinida
        if (indexNames >= predefinedNames.size()) {
            indexNames = 0; // Reiniciar el índice si alcanza el límite
        }
        return predefinedNames.get(indexNames++);
    }

    private String getNextPredefinedLastName() {
        // Obtener el siguiente apellido de la lista predefinida
        if (indexLastNames >= predefinedLastNames.size()) {
            indexLastNames = 0; // Reiniciar el índice si alcanza el límite
        }
        return predefinedLastNames.get(indexLastNames++);
    }

    private String getNextPredefinedEmail() {
        // Obtener el siguiente correo electrónico de la lista predefinida
        if (indexEmailUser >= predefinedEmails.size()) {
            indexEmailUser = 0; // Reiniciar el índice si alcanza el límite
        }
        return predefinedEmails.get(indexEmailUser++);
    }

    private String getNextPredefinedEmailAdmin() {
        // Obtener el siguiente correo electrónico de la lista predefinida para los administradores
        if (indexEmailAdmin >= predefinedEmailsAdmin.size()) {
            indexEmailAdmin = 0; // Reiniciar el índice si alcanza el límite
        }
        return predefinedEmailsAdmin.get(indexEmailAdmin++);
    }

    private LocalDate generateRandomBirthDate() {
        // Definir el rango de fechas para las fechas de nacimiento aleatorias
        LocalDate minDate = LocalDate.of(1950, 1, 1); // Fecha mínima
        LocalDate maxDate = LocalDate.of(2005, 12, 31); // Fecha máxima

        // Calcular un valor aleatorio dentro del rango de fechas
        long minDay = minDate.toEpochDay();
        long maxDay = maxDate.toEpochDay();
        long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay);

        // Crear una fecha aleatoria utilizando el valor aleatorio
        return LocalDate.ofEpochDay(randomDay);
    }
}
