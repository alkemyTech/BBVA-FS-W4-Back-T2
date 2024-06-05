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
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class TransactionSeeder implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public void run(String... args) throws Exception {
        createRandomTransactionsForAccounts(1, 20);
    }

    private void createRandomTransactionsForAccounts(int startId, int endId) {
        Random random = new Random();

        for (int accountId = startId; accountId <= endId; accountId++) {
            Accounts account = accountRepository.findById((long) accountId).orElse(null);
            if (account == null) continue;

            IntStream.range(0, 20).forEach(i -> {
                double amount = random.nextDouble() * 10000; // Monto aleatorio entre 0 y 10,000
                CurrencyEnum currency = account.getCurrency(); // Usar la moneda de la cuenta
                TransactionEnum type = TransactionEnum.values()[random.nextInt(TransactionEnum.values().length)];
                String description = "Transaction " + type + " of " + amount + " " + currency;

                Transaction transaction = new Transaction();
                transaction.setAmount(amount);
                transaction.setType(type);
                transaction.setDescription(description);
                transaction.setTransactionDate(LocalDateTime.now());
                transaction.setAccountId(account);

                switch (type) {
                    case DEPOSIT:
                        transactionRepository.save(transaction);
                        break;
                    case PAYMENT:
                    case INCOME:
                        Accounts destinationAccount = getRandomAccount(startId, endId, random);
                        if (destinationAccount != null) {
                            transaction.setAccountId(destinationAccount);
                            transaction.setOriginAccount(account);
                            transactionRepository.save(transaction);
                        }
                        break;
                }
            });
        }
    }

    private Accounts getRandomAccount(int startId, int endId, Random random) {
        Long randomAccountId = (long) (startId + random.nextInt(endId - startId + 1));
        return accountRepository.findById(randomAccountId).orElse(null);
    }
}
