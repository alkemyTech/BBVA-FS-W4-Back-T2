package AlkemyWallet.AlkemyWallet.repositories.seeders;
import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.domain.Role;

import AlkemyWallet.AlkemyWallet.domain.factory.RoleFactory;
import AlkemyWallet.AlkemyWallet.enums.RoleEnum;
import AlkemyWallet.AlkemyWallet.repositories.RoleRepository;
import AlkemyWallet.AlkemyWallet.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
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
    private int index = 0;

    private List<String> predefinedNames = List.of("Juan", "María", "Frank", "Luk", "Ricardo","Moria", "Mirtha", "Duki", "Lionel", "Lizy");
    private List<String> predefinedLastNames =List.of("Pecados","Becerra", "Sinatra", "Ra","Fort", "Casan", "Legrand","Lombardo", "Messi", "Tagliani" );
    private List<String> predefinedEmails = List.of("juan@example.com", "maria@example.com", "pedro@example.com", "ana@example.com", "luis@example.com", "roberto@example.com", "pablo@example.com", "duki@example.com", "messi@example.com", "liliana@example.com");
    private List<String> predefinedEmailsAdmin = List.of("juan@exampleAdmin.com", "maria@exampleAdmin.com", "pedro@exampleAdmin.com", "ana@exampleAdmin.com", "luis@exampleAdmin.com", "roberto@exampleAdmin.com", "pablo@exampleAdmin.com", "duki@exampleAdmin.com", "messi@exampleAdmin.com", "liliana@exampleAdmin.com");

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
                    .build();
            userRepository.save(user);
        }


private void createRegularUser() {

    User user = User.builder()
            .userName(getNextPredefinedEmail())
            .password(passwordEncoder.encode("adminPassword"))
            .firstName(getNextPredefinedName())
            .lastName(getNextPredefinedLastName())
            .birthDate(generateRandomBirthDate())
            .role(RoleFactory.getUserRole())
            .creationDate(LocalDateTime.now())
            .updateDate(LocalDateTime.now())
            .build();
    userRepository.save(user);
}

    private String getNextPredefinedName() {
        // Obtener el siguiente nombre de la lista predefinida
        if (index >= predefinedNames.size()) {
            index = 0; // Reiniciar el índice si alcanza el límite
        }
        return predefinedNames.get(index++);
    }

    private String getNextPredefinedLastName() {
        // Obtener el siguiente apellido de la lista predefinida
        if (index >= predefinedLastNames.size()) {
            index = 0; // Reiniciar el índice si alcanza el límite
        }
        return predefinedLastNames.get(index++);
    }

    private String getNextPredefinedEmail() {
        // Obtener el siguiente correo electrónico de la lista predefinida
        if (index >= predefinedEmails.size()) {
            index = 0; // Reiniciar el índice si alcanza el límite
        }
        return predefinedEmails.get(index++);
    }

    private String getNextPredefinedEmailAdmin() {
        // Obtener el siguiente correo electrónico de la lista predefinida para los administradores
        if (index >= predefinedEmailsAdmin.size()) {
            index = 0; // Reiniciar el índice si alcanza el límite
        }
        return predefinedEmailsAdmin.get(index++);
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