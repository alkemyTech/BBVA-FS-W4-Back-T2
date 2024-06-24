package AlkemyWallet.AlkemyWallet.repositories.seeders;

import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.dtos.TransactionDTO;
import AlkemyWallet.AlkemyWallet.enums.AccountTypeEnum;
import AlkemyWallet.AlkemyWallet.enums.CurrencyEnum;
import AlkemyWallet.AlkemyWallet.repositories.AccountRepository;
import AlkemyWallet.AlkemyWallet.repositories.UserRepository;
import AlkemyWallet.AlkemyWallet.services.AccountService;
import AlkemyWallet.AlkemyWallet.services.TransactionService;
import AlkemyWallet.AlkemyWallet.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;

@Component
public class AccountsSeeder {

    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;
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

            if (userRepository.findById(userId).isPresent()) {
                User user = userRepository.findById(userId).get();

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
        account.setCreationDate(LocalDateTime.now());
        account.setUpdateDate(LocalDateTime.now());
        account.setTransactionLimit(currency.getTransactionLimit());
        account.setSoftDelete(false);
        account.setCBU(accountService.generarCBU());
        account.setAlias(accountService.generarAlias(account));

            double depositAmount = 0.00;
                depositAmount = Math.round((Math.random() * 300000) * 100.0) / 100.0;

            account.setBalance(depositAmount);


        accountRepository.save(account);
    }



}