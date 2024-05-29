package AlkemyWallet.AlkemyWallet.services;

import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.domain.Transaction;
import AlkemyWallet.AlkemyWallet.domain.factory.TransactionFactory;
import AlkemyWallet.AlkemyWallet.dtos.TransactionDTO;
import AlkemyWallet.AlkemyWallet.enums.CurrencyEnum;
import AlkemyWallet.AlkemyWallet.enums.TransactionEnum;
import AlkemyWallet.AlkemyWallet.exceptions.InsufficientFundsException;
import AlkemyWallet.AlkemyWallet.repositories.TransactionRepository;
import AlkemyWallet.AlkemyWallet.exceptions.IncorrectCurrencyException;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;


@Service
@AllArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final TransactionFactory transactionFactory;

    public Object registrarTransaccion(TransactionDTO transaction, Accounts originAccount) {
        Double amount = transaction.getAmount();
        Accounts destinationAccount = accountService.findByCBU(transaction.getDestino());

        CurrencyEnum transactionCurrency;
        try {
            transactionCurrency = CurrencyEnum.valueOf(transaction.getCurrency().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Moneda no válida: " + transaction.getCurrency());
        }

        if (!transactionCurrency.equals(originAccount.getCurrency())) {
            throw new IncorrectCurrencyException("La moneda seleccionada no es la correcta para este tipo de cuenta");
        }

        if (!originAccount.dineroDisponible(amount) || !originAccount.limiteDisponible(amount)) {
            throw new InsufficientFundsException("No hay suficiente dinero o límite disponible para completar la transacción");
        }

        Long idTransaction = this.sendMoney(transaction, originAccount, destinationAccount);
        this.receiveMoney(transaction, originAccount, destinationAccount);
        accountService.updateAfterTransaction(originAccount, amount);

        return idTransaction;
    }

    public Long sendMoney(TransactionDTO transaction, Accounts originAccount, Accounts destinationAccount) {
        Transaction paymentTransaction = transactionFactory.createTransaction(
                transaction.getAmount(),
                TransactionEnum.PAYMENT,
                "",
                LocalDateTime.now(),
                destinationAccount,
                originAccount
        );

        transactionRepository.save(paymentTransaction);
        return paymentTransaction.getId();
    }

    public void receiveMoney(TransactionDTO transaction, Accounts originAccount, Accounts destinationAccount) {
        Transaction incomeTransaction = transactionFactory.createTransaction(
                transaction.getAmount(),
                TransactionEnum.INCOME,
                "",
                LocalDateTime.now(),
                destinationAccount,
                originAccount
        );
        transactionRepository.save(incomeTransaction);
    }

}





