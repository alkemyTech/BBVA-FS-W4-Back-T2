package AlkemyWallet.AlkemyWallet.repositories;

import AlkemyWallet.AlkemyWallet.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

   Optional<User> findById(Long id);

   Optional<User> findByUserName(String userName);

   @Modifying
   @Query("UPDATE User u SET u.softDelete = 1 WHERE u.id = :id")
   void softDeleteById(Long id);
}

