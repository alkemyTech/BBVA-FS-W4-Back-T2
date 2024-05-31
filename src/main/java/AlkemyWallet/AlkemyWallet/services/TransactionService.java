package AlkemyWallet.AlkemyWallet.services;

import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.domain.Transaction;
import AlkemyWallet.AlkemyWallet.repositories.UserRepository;
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
import java.util.List;


@Service
@AllArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final TransactionFactory transactionFactory;
    private final UserRepository userRepository;
    private final UserService userService;

    public Object registrarTransaccion(TransactionDTO transaction, Accounts account) {
        Double amount = transaction.getAmount();
        Accounts accountDestino = accountService.findByCBU(transaction.getDestino());

        CurrencyEnum transactionCurrency;
        try {
            transactionCurrency = CurrencyEnum.valueOf(transaction.getCurrency().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Moneda no válida: " + transaction.getCurrency());
        }

        if (!transactionCurrency.equals(account.getCurrency())) {
            throw new IncorrectCurrencyException("La moneda seleccionada no es la correcta para este tipo de cuenta");
        }

        if (!account.dineroDisponible(amount) || !account.limiteDisponible(amount)) {
            throw new InsufficientFundsException("No hay suficiente dinero o límite disponible para completar la transacción");
        }

        Long idTransaction = this.sendMoney(transaction, accountDestino);
        this.receiveMoney(transaction, account);
        accountService.updateAfterTransaction(account, amount);

        return idTransaction;
    }


    public Long sendMoney(TransactionDTO transaction, Accounts account){
        Transaction paymentTransaction = transactionFactory.createTransaction(
                transaction.getAmount(),
                TransactionEnum.PAYMENT,
                "",
                LocalDateTime.now(),
                account.getId()
        );


        transactionRepository.save(paymentTransaction);
        return paymentTransaction.getId();
    }

    public void receiveMoney(TransactionDTO transaction, Accounts account){
        Transaction incomeTransaction = transactionFactory.createTransaction(
                transaction.getAmount(),
                TransactionEnum.INCOME,
                "",
                LocalDateTime.now(),
                account.getId()
        );
        transactionRepository.save(incomeTransaction);
    }
    public List<Transaction> getTransactionsByAccount(Accounts account) {
        try {
            return transactionRepository.findByAccountId(account);
        } catch (Exception e) {
            throw new RuntimeException("No se encontraron transacciones para la cuenta", e);
        }
    }

    public List<Transaction> getTransactionsByAccountId(Long accountId) {
        try {
            Accounts account = accountService.findById(accountId); // Obtener la cuenta completa
            return getTransactionsByAccount(account);
        } catch (Exception e) {
            throw new RuntimeException("No se encontraron transacciones para la cuenta", e);
        }
    }
}



