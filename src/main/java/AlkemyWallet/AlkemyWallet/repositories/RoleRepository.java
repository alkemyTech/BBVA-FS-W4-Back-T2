package AlkemyWallet.AlkemyWallet.repositories;

import AlkemyWallet.AlkemyWallet.domain.Role;
import AlkemyWallet.AlkemyWallet.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    public Role findByName(RoleEnum name);
}
