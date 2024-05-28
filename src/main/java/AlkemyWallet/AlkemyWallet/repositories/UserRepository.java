package AlkemyWallet.AlkemyWallet.repositories;

import AlkemyWallet.AlkemyWallet.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

   Optional<User> findById(Long id);

   Optional<User> findByUserName(String userName);

   void deleteById(Long id);
}
