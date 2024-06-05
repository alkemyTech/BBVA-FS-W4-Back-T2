package AlkemyWallet.AlkemyWallet.seeders;

import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.dtos.TransactionDTO;
import AlkemyWallet.AlkemyWallet.enums.AccountTypeEnum;
import AlkemyWallet.AlkemyWallet.enums.CurrencyEnum;
import AlkemyWallet.AlkemyWallet.repositories.AccountRepository;
import AlkemyWallet.AlkemyWallet.services.AccountService;
import AlkemyWallet.AlkemyWallet.services.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
@AllArgsConstructor
public class AccountsSeeder {

    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final TransactionService transactionService;

    public void seedAccounts() {
        Random random = new Random();

        // Crear cuentas para usuarios
        for (int i = 1; i <= 20; i++) {
            User user = new User();
            user.setId((long) i);
            // Asignar cuentas según el rango de ID
            if (i <= 10) {
                // USER
                createAccounts(user, AccountTypeEnum.CAJA_AHORRO, CurrencyEnum.ARS);
                createAccounts(user, AccountTypeEnum.CAJA_AHORRO, CurrencyEnum.USD);
            } else {
                // ADMIN
                createAccounts(user, AccountTypeEnum.CAJA_AHORRO, CurrencyEnum.ARS);
                createAccounts(user, AccountTypeEnum.CAJA_AHORRO, CurrencyEnum.USD);
            }
        }

        // Realizar depósitos
        depositToAllAccounts();
    }

    public void depositToAllAccounts() {
        // Obtener todas las cuentas
        //Usar el otro metodo, el que va a la ruta /Accounts
        List<Accounts> allAccounts = accountService.getAllAccounts();

        // Iterar sobre todas las cuentas
        for (Accounts account : allAccounts) {
            try {
                // Verificar si la cuenta es de tipo ARS y el límite es menor o igual a 300000
                if (account.getCurrency() == CurrencyEnum.ARS && account.getTransactionLimit() <= 300000) {
                    // Generar un monto aleatorio para el depósito (puedes ajustar la lógica según tus necesidades)
                    double depositAmount = Math.random() * 300000;

                    // Crear la transacción de depósito
                    TransactionDTO depositTransaction = new TransactionDTO();
                    depositTransaction.setAmount(depositAmount);
                    depositTransaction.setDestino(account.getCBU());
                    Long transactionId = transactionService.depositMoney(depositTransaction, account);

                    // Imprimir mensaje de éxito
                    System.out.println("Se realizó un depósito de " + depositAmount + " ARS en la cuenta con ID " + account.getId());
                }
                // Verificar si la cuenta es de tipo USD y el límite es menor o igual a 1000
                else if (account.getCurrency() == CurrencyEnum.USD && account.getTransactionLimit() <= 1000) {
                    // Generar un monto aleatorio para el depósito (puedes ajustar la lógica según tus necesidades)
                    double depositAmount = Math.random() * 1000;

                    // Crear la transacción de depósito
                    TransactionDTO depositTransaction = new TransactionDTO();
                    depositTransaction.setAmount(depositAmount);
                    depositTransaction.setDestino(account.getCBU());
                    Long transactionId = transactionService.depositMoney(depositTransaction, account);

                    // Imprimir mensaje de éxito
                    System.out.println("Se realizó un depósito de " + depositAmount + " USD en la cuenta con ID " + account.getId());
                }
            } catch (Exception e) {
                // Manejar cualquier excepción ocurrida durante el depósito
                System.err.println("Error al realizar el depósito en la cuenta con ID " + account.getId() + ": " + e.getMessage());
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
        account.setCBU(generateCBU());
        accountRepository.save(account);
    }

    private String generateCBU() {
        // Lógica para generar CBU
        return "CBU" + (new Random().nextInt(900000) + 100000);
    }
}