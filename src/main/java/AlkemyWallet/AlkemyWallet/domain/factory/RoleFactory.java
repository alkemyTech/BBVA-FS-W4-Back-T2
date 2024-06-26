package AlkemyWallet.AlkemyWallet.domain.factory;

import AlkemyWallet.AlkemyWallet.domain.Role;
import AlkemyWallet.AlkemyWallet.enums.RoleEnum;
import AlkemyWallet.AlkemyWallet.repositories.RoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Data
public class RoleFactory {

    private final RoleRepository roleRepository;

    @Getter
    private static Role adminRole;
    @Getter
    private static Role userRole;

    @PostConstruct
    public void initializeRoles() {
        adminRole = roleRepository.findByName(RoleEnum.ADMIN);
        if (adminRole == null) {
            adminRole = roleRepository.save(new Role(null, RoleEnum.ADMIN, "Administrator role", LocalDateTime.now(), LocalDateTime.now()));
        }

        userRole = roleRepository.findByName(RoleEnum.USER);
        if (userRole == null) {
            userRole = roleRepository.save(new Role(null, RoleEnum.USER, "User role", LocalDateTime.now(), LocalDateTime.now()));
        }
    }

}
