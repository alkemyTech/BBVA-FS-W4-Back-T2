package AlkemyWallet.AlkemyWallet.repositories;

import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.domain.Transaction;
import AlkemyWallet.AlkemyWallet.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository <Transaction, Long> {
    @Query("SELECT t FROM Transaction t WHERE t.originAccount.id = :accountId")
    List<Transaction> findByAccountId(Long accountId);

    List<Transaction> findByAccount(Accounts account);

    Page<Transaction> findByAccountIdUserId(User userId, Pageable pageable);
}