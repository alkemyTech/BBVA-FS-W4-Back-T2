package AlkemyWallet.AlkemyWallet.services;

import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.domain.Transaction;
import AlkemyWallet.AlkemyWallet.repositories.UserRepository;
import AlkemyWallet.AlkemyWallet.domain.factory.TransactionFactory;
import AlkemyWallet.AlkemyWallet.dtos.TransactionDTO;
import AlkemyWallet.AlkemyWallet.dtos.TransactionResponse;
import AlkemyWallet.AlkemyWallet.enums.CurrencyEnum;
import AlkemyWallet.AlkemyWallet.enums.TransactionEnum;
import AlkemyWallet.AlkemyWallet.exceptions.InsufficientFundsException;
import AlkemyWallet.AlkemyWallet.exceptions.NonPositiveAmountException;
import AlkemyWallet.AlkemyWallet.exceptions.UnauthorizedTransactionException;
import AlkemyWallet.AlkemyWallet.mappers.TransactionResponseMapper;
import AlkemyWallet.AlkemyWallet.repositories.TransactionRepository;
import AlkemyWallet.AlkemyWallet.exceptions.IncorrectCurrencyException;


import lombok.AllArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


@Service
@AllArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final TransactionFactory transactionFactory;
    private final UserRepository userRepository;
    private final UserService userService;
    private final JwtService jwtService;
    private final TransactionResponseMapper transactionResponseMapper;

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

        Transaction transactionRegistro = this.sendMoney(transaction, originAccount, destinationAccount);
        this.receiveMoney(transaction, originAccount, destinationAccount);
        accountService.updateAfterTransaction(originAccount, amount);
        accountService.updateAfterTransaction(destinationAccount, -amount);

        return transactionResponseMapper.mapToTransactionResponse(transactionRegistro, originAccount, destinationAccount);
    }


    public Transaction sendMoney(TransactionDTO transaction, Accounts originAccount, Accounts destinationAccount) {
        Transaction paymentTransaction = transactionFactory.createTransaction(
                transaction.getAmount(),
                TransactionEnum.PAYMENT,
                "",
                LocalDateTime.now(),
                destinationAccount,
                originAccount
        );

        transactionRepository.save(paymentTransaction);
        return paymentTransaction;
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
    public List<Transaction> getTransactionsByAccount(Accounts account) {
        try {
            return transactionRepository.findByAccountId(account);
        } catch (Exception e) {
            throw new RuntimeException("No se encontraron transacciones para la cuenta", e);
        }
    }

    public Long depositMoney(TransactionDTO transaction, Accounts account) {
        try {
            Accounts destinationAccount = accountService.findByCBU(transaction.getDestino());
            validateDepositTransaction(transaction,account,destinationAccount);

            // Crear una transacción de depósito para la cuenta de origen
            Transaction depositTransaction = transactionFactory.createTransaction(
                    transaction.getAmount(),
                    TransactionEnum.DEPOSIT,
                    "",
                    LocalDateTime.now(),
                    account,
                    account // La cuenta de origen es la misma que la cuenta de destino en un depósito
            );
            transactionRepository.save(depositTransaction);

            // Actualizar el saldo de la cuenta de origen
            accountService.updateAfterTransaction(account, -transaction.getAmount());

            return depositTransaction.getId();
        } catch (NonPositiveAmountException e) {
            System.err.println(e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Se produjo un error inesperado al procesar el depósito: " + e.getMessage());
            throw new RuntimeException("Error al procesar el depósito");
        }
    }

    private void validateDepositTransaction(TransactionDTO transaction, Accounts account, Accounts destinationAccount) {
        if (transaction.getAmount() <= 0) {
            throw new NonPositiveAmountException("El monto del depósito debe ser mayor que cero");
        }
        if(!Objects.equals(account.getCBU(), destinationAccount.getCBU())) {
            throw new UnauthorizedTransactionException("Para realizar un deposito, la cuenta origen debe coincidir con la cuenta destino");
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



