package AlkemyWallet.AlkemyWallet.repositories;

import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.domain.UserContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface userContactRepository extends JpaRepository<UserContact,Long> {

    @Query("SELECT a FROM UserContact a WHERE a.CBU = :cbu")
    Optional<UserContact> findByCbu(@Param("cbu") String cbu);

    List<UserContact> findByUser(User user);
}
