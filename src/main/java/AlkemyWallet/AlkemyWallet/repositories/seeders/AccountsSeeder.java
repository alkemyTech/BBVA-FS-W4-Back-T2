package AlkemyWallet.AlkemyWallet.repositories.seeders;

import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.dtos.TransactionDTO;
import AlkemyWallet.AlkemyWallet.enums.AccountTypeEnum;
import AlkemyWallet.AlkemyWallet.enums.CurrencyEnum;
import AlkemyWallet.AlkemyWallet.repositories.AccountRepository;
import AlkemyWallet.AlkemyWallet.services.AccountService;
import AlkemyWallet.AlkemyWallet.services.TransactionService;
import AlkemyWallet.AlkemyWallet.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class AccountsSeeder {

    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private UserService userService;


    public void seedAccounts() {
        Random random = new Random();
        Long userId = 0L;
        // Crear cuentas para usuarios
        for (int i = 1; i <= 20; i++) {

            userId=(long)i;

            if (userService.findById(userId).isPresent()) {
                User user = userService.findById(userId).get();

                if (i <= 10) {
                    // USER
                    createAccounts(user, AccountTypeEnum.CAJA_AHORRO, CurrencyEnum.ARS);
                    createAccounts(user, AccountTypeEnum.CAJA_AHORRO, CurrencyEnum.USD);
                } else {
                    // ADMIN
                    createAccounts(user, AccountTypeEnum.CAJA_AHORRO, CurrencyEnum.ARS);
                    createAccounts(user, AccountTypeEnum.CAJA_AHORRO, CurrencyEnum.USD);
                }

            } else {
                throw new IllegalStateException("El usuario no se encontró");
            }

        }
    }

    private void createAccounts(User user, AccountTypeEnum accountType, CurrencyEnum currency) {
        Accounts account = new Accounts();
        account.setUserId(user);
        account.setAccountType(accountType);
        account.setCurrency(currency);
        account.setTransactionLimit(currency.getTransactionLimit());
        account.setBalance(0.0); // Saldo inicial cero
        account.setCBU(accountService.generarCBU());
        accountRepository.save(account);
        // Llamar al método para depositar el dinero en la cuenta recién creada
        depositToAccount(account);
    }

    private void depositToAccount(Accounts account) {
        try {
            double depositAmount;
            if (account.getCurrency() == CurrencyEnum.ARS && account.getTransactionLimit() <= 300000) {
                depositAmount = Math.round((Math.random() * 300000) * 100.0) / 100.0;
            } else if (account.getCurrency() == CurrencyEnum.USD && account.getTransactionLimit() <= 1000) {
                depositAmount = Math.round((Math.random() * 1000) * 100.0) / 100.0;;
            } else {
                // No se cumple ninguna condición, no se realiza el depósito
                return;
            }

            // Crear la transacción de depósito

            TransactionDTO depositTransaction = new TransactionDTO();
            depositTransaction.setAmount(depositAmount);
            depositTransaction.setDestino(account.getCBU());
            Long transactionId = transactionService.depositMoney(depositTransaction, account);

            // Imprimir mensaje de éxito
            System.out.println("Se realizó un depósito de " + depositAmount + " en la cuenta con ID " + account.getId());
        } catch (Exception e) {
            // Manejar cualquier excepción ocurrida durante el depósito
            System.err.println("Error al realizar el depósito en la cuenta con ID " + account.getId() + ": " + e.getMessage());
        }
    }

}