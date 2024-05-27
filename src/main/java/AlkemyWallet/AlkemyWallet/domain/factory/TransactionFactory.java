package AlkemyWallet.AlkemyWallet.domain.factory;

import AlkemyWallet.AlkemyWallet.domain.Transaction;
import AlkemyWallet.AlkemyWallet.enums.TransactionEnum;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TransactionFactory{

    @Bean
    public Transaction createTransaction(double amount, TransactionEnum type, String description, LocalDateTime transactionDate, Long accountId) {
        return new Transaction(amount, type, description, transactionDate, accountId);
    }
}