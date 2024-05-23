package AlkemyWallet.AlkemyWallet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FixedTermDeposit extends JpaRepository<FixedTermDeposit, Long> {
}
