package AlkemyWallet.AlkemyWallet.domain.factory;

import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.domain.Transaction;
import AlkemyWallet.AlkemyWallet.enums.TransactionEnum;
import AlkemyWallet.AlkemyWallet.services.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@AllArgsConstructor
public class TransactionFactory{

    private final AccountService accountService;


    public Transaction createTransaction(Double amount, TransactionEnum type, String description, LocalDateTime transactionDate, Long accountId) {
        Accounts account = accountService.findById(accountId);
        return new Transaction (amount, type, description, transactionDate, account);
    }
}