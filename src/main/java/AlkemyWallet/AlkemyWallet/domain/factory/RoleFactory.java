package AlkemyWallet.AlkemyWallet.domain.factory;

import AlkemyWallet.AlkemyWallet.domain.Role;
import AlkemyWallet.AlkemyWallet.enums.RoleEnum;
import lombok.Data;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Data
public class RoleFactory {
    @Getter
    private static Role adminRole;
    // Método para obtener la instancia de USER
    @Getter
    private static Role userRole;


    static {
        adminRole = new Role(null,RoleEnum.ADMIN, "Administrator role", LocalDateTime.now(), LocalDateTime.now());
        userRole = new Role(null,RoleEnum.USER, "User role", LocalDateTime.now(), LocalDateTime.now());
    }


}
