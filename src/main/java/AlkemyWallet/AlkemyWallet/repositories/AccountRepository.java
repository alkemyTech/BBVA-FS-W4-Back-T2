package AlkemyWallet.AlkemyWallet.repositories;
import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Accounts, Long> {
    Optional<Accounts> findByCBU(String CBU);
    List<Accounts> findByUserId(User userId);
  
}