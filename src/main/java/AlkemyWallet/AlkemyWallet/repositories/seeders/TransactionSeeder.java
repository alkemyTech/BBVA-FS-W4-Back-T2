package AlkemyWallet.AlkemyWallet.repositories.seeders;

import AlkemyWallet.AlkemyWallet.domain.Transaction;
import AlkemyWallet.AlkemyWallet.enums.TransactionEnum;
import AlkemyWallet.AlkemyWallet.repositories.AccountRepository;
import AlkemyWallet.AlkemyWallet.repositories.TransactionRepository;
import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.enums.CurrencyEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class TransactionSeeder implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public void run(String... args) throws Exception {
        createRandomTransactionsForAccounts(1, 20, 40); // Limitado a 40 transacciones en total
    }

    private void createRandomTransactionsForAccounts(int startId, int endId, int totalTransactions) {
        Random random = new Random();
        int depositCount = 0;
        int paymentCount = 0;
        int incomeCount = 0;

        while (depositCount + paymentCount + incomeCount < totalTransactions) {
            for (int accountId = startId; accountId <= endId && (depositCount + paymentCount + incomeCount) < totalTransactions; accountId++) {
                Accounts account = accountRepository.findById((long) accountId).orElse(null);
                if (account == null) continue;

                double amount = random.nextDouble() * 10000; // Monto aleatorio entre 0 y 10,000
                CurrencyEnum currency = account.getCurrency(); // Usar la moneda de la cuenta

                Transaction transaction = new Transaction();
                transaction.setAmount(amount);
                transaction.setTransactionDate(LocalDateTime.now());

                if (depositCount < 20) {
                    transaction.setType(TransactionEnum.DEPOSIT);
                    transaction.setDescription("Transaction DEPOSIT of " + amount + " " + currency);
                    transaction.setAccountId(account);
                    transaction.setOriginAccount(account);
                    transactionRepository.save(transaction);
                    depositCount++;
                } else if (paymentCount < 10) {
                    Accounts destinationAccount = getRandomAccount(startId, endId, random, account);
                    if (destinationAccount != null && !account.equals(destinationAccount)) {
                        transaction.setType(TransactionEnum.PAYMENT);
                        transaction.setDescription("Transaction PAYMENT of " + amount + " " + currency);
                        transaction.setAccountId(destinationAccount);
                        transaction.setOriginAccount(account);
                        transactionRepository.save(transaction);
                        paymentCount++;

                        // Crear la transacción INCOME correspondiente
                        Transaction incomeTransaction = new Transaction();
                        incomeTransaction.setAmount(amount);
                        incomeTransaction.setType(TransactionEnum.INCOME);
                        incomeTransaction.setDescription("Transaction INCOME of " + amount + " " + currency);
                        incomeTransaction.setTransactionDate(LocalDateTime.now());
                        incomeTransaction.setAccountId(account);
                        incomeTransaction.setOriginAccount(destinationAccount);
                        transactionRepository.save(incomeTransaction);
                        incomeCount++;
                    }
                }
            }
        }
    }

    private Accounts getRandomAccount(int startId, int endId, Random random, Accounts excludeAccount) {
        Long randomAccountId;
        Accounts randomAccount;
        do {
            randomAccountId = (long) (startId + random.nextInt(endId - startId + 1));
            randomAccount = accountRepository.findById(randomAccountId).orElse(null);
        } while (randomAccount == null || randomAccount.equals(excludeAccount));
        return randomAccount;
    }
}
