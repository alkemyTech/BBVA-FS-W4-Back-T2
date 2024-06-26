package AlkemyWallet.AlkemyWallet.repositories;

import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.domain.Transaction;
import AlkemyWallet.AlkemyWallet.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository <Transaction, Long> {
    @Query("SELECT t FROM Transaction t WHERE t.account.id = :accountId")
    List<Transaction> findByAccountId(Long accountId);

    @Query("SELECT t FROM Transaction t WHERE t.originAccount.id = :accountId")
    List<Transaction> findByOriginAccountId(Long accountId);

    @Query("SELECT t FROM Transaction t WHERE t.originAccount.id = :accountId AND t.type = 'DEPOSIT'")
    List<Transaction> findDepositsByAccountId(@Param("accountId") Long accountId);

    @Query("SELECT t FROM Transaction t WHERE t.originAccount.id = :accountId AND t.type = 'PAYMENT'")
    List<Transaction> findPaymentsByAccountId(@Param("accountId") Long accountId);

    @Query("SELECT t FROM Transaction t WHERE t.account.id = :accountId AND t.type = 'INCOME'")
    List<Transaction> findIncomesByAccountId(@Param("accountId") Long accountId);

    @Query("SELECT t FROM Transaction t WHERE t.originAccount.id = :accountId or t.account.id =:accountId")
    Page<Transaction> findByOriginAccountOrAccount(Long accountId, Pageable pageable);

    Page<Transaction> findAll(Specification<Transaction> spec, Pageable pageable);
}