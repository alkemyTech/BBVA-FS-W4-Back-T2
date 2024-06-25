package AlkemyWallet.AlkemyWallet.domain.factory;

import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.domain.Transaction;
import AlkemyWallet.AlkemyWallet.enums.TransactionEnum;
import AlkemyWallet.AlkemyWallet.services.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@AllArgsConstructor
public class TransactionFactory {

    public Transaction createTransaction(Double amount, TransactionEnum type, String description, LocalDateTime transactionDate, Accounts destinationAccount, Accounts originAccount) {
        return new Transaction(amount, type, description, transactionDate, destinationAccount, originAccount);
    }

    public Transaction createTransactionPayment(Double amount,  String description, LocalDateTime transactionDate, Accounts originAccount, String destinoExterno) {
        return new Transaction(amount, TransactionEnum.PAYMENT, description, transactionDate,  originAccount, destinoExterno);
    }
}
