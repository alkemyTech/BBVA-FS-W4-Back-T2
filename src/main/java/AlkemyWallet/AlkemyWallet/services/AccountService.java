package AlkemyWallet.AlkemyWallet.services;

import AlkemyWallet.AlkemyWallet.config.PaginationConfig;
import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.domain.FixedTermDeposit;
import AlkemyWallet.AlkemyWallet.domain.Transaction;
import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.dtos.*;
import AlkemyWallet.AlkemyWallet.dtos.AccountRequestDto;
import AlkemyWallet.AlkemyWallet.dtos.AccountsDto;
import AlkemyWallet.AlkemyWallet.enums.AccountTypeEnum;
import AlkemyWallet.AlkemyWallet.enums.CurrencyEnum;
import AlkemyWallet.AlkemyWallet.exceptions.*;
import AlkemyWallet.AlkemyWallet.repositories.AccountRepository;
import AlkemyWallet.AlkemyWallet.repositories.TransactionRepository;
import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import AlkemyWallet.AlkemyWallet.exceptions.DuplicateAccountException;
import AlkemyWallet.AlkemyWallet.exceptions.UserNotFoundException;

import java.util.*;
import java.util.Random;
import java.util.List;

@Service
public class AccountService {
    @Autowired
    private  AccountRepository accountRepository;
    @Autowired
    private  UserService userService;
    @Autowired
    private  JwtService jwtService;
    @Autowired
    private  PaginationConfig paginationConfig;



    public AccountsDto add(AccountRequestDto accountCreation, HttpServletRequest request) {

        String currency = accountCreation.getCurrency();
        String accountType = accountCreation.getAccountType();
        CurrencyEnum currencyEnum = CurrencyEnum.valueOf(currency);
        AccountTypeEnum accountTypeEnum = AccountTypeEnum.valueOf(accountType);
        Long userId = userService.getIdFromRequest(request);
        User user = userService.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        //Validacion...

        if (verificarExistenciaAccount(user, currencyEnum, accountTypeEnum)) {
            throw new DuplicateAccountException("No se puede tener mas de un tipo de cuenta con la misma moneda");
        }

        try {
            Accounts account = new Accounts();
            account.setCurrency(currencyEnum);
            account.setAccountType(accountTypeEnum);
            account.setTransactionLimit(currencyEnum.getTransactionLimit());
            account.setBalance(0.00);
            account.setCBU(generarCBU());
            account.setUserId(user);  // --> JWT
            account.setCurrency(currencyEnum);

            Accounts savedAccount = accountRepository.save(account);

            return accountMapper(savedAccount);
        } catch (Exception e) {
            throw new RuntimeException("Error al agregar la cuenta", e);
        }
    }

    public List<Accounts> findAccountsByUserId(Long userId) {
        try{
            User user = userService.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            return accountRepository.findByUserId(user);
        }catch (Exception e){
            throw new RuntimeException("No se encontró al usuario",e);
        }
    }

    public AccountsDto addById(AccountRequestDto accountCreation, Long userId) {

        String currency = accountCreation.getCurrency();
        String accountType = accountCreation.getAccountType();
        CurrencyEnum currencyEnum = CurrencyEnum.valueOf(currency);
        AccountTypeEnum accountTypeEnum = AccountTypeEnum.valueOf(accountType);

        if (userService.findById(userId).isPresent()) {
            User user = userService.findById(userId).get();
            if (!verificarExistenciaAccount(user, currencyEnum, accountTypeEnum)) {
                try {
                    Accounts account = new Accounts();
                    account.setCurrency(currencyEnum);
                    account.setAccountType(accountTypeEnum);
                    account.setTransactionLimit(currencyEnum.getTransactionLimit());
                    account.setBalance(0.00);
                    account.setCBU(generarCBU());
                    account.setUserId(user);  // --> JWT
                    account.setCurrency(currencyEnum);

                    Accounts savedAccount = accountRepository.save(account);


                    return accountMapper(savedAccount);
                } catch (Exception e) {
                    throw new RuntimeException("Error al agregar la cuenta", e);
                }
            }else{
                throw new IllegalArgumentException("No se puede tener mas de un tipo de cuenta con la misma moneda");
            }
        } else {
            throw new IllegalStateException("Usuario no encontrado");
        }
    }

    public static String logicaCBU () {
        StringBuilder cbu = new StringBuilder();
        Random random = new Random();


            // Primeros 7 dígitos corresponden al código del banco y de la sucursal.
            for (int i = 0; i < 7; i++) {
                cbu.append(random.nextInt(10));
            }
            cbu.append("0"); // Agregamos un dígito fijo para el dígito verificador provisorio.

            // Los siguientes 12 dígitos son generados aleatoriamente.
            for (int i = 0; i < 12; i++) {
                cbu.append(random.nextInt(10));
            }

            // Calculamos el dígito verificador provisorio.
            int[] weights = {3, 1, 7, 9, 3, 1, 7, 9, 3, 1, 7, 9, 3, 1, 3, 1, 7, 9, 3, 1, 7, 9};
            int sum = 0;
            for (int i = 0; i < cbu.length(); i++) {
                sum += (Character.getNumericValue(cbu.charAt(i)) * weights[i]);
            }
            int dv = (10 - (sum % 10)) % 10;
            cbu.setCharAt(7, Character.forDigit(dv, 10));

            return cbu.toString();
        }

    public String generarCBU () {
            String CBU = null;
            boolean cbuExistente = true;

            // Genera un nuevo CBU hasta que encuentres uno que no exista en la base de datos
            while (cbuExistente) {
                // Genera un nuevo CBU
                CBU = logicaCBU();

                // Verifica si el CBU generado ya existe en la base de datos
                if (!accountRepository.findByCBU(CBU).isPresent()) {
                    // Si el CBU no existe, sal del bucle
                    cbuExistente = false;
                }
            }

            return CBU;
        }

    public void updateAfterTransaction(Accounts account, Double amount) {
        account.updateBalance(amount);
        account.updateLimit(amount);
        accountRepository.save(account);
    }

    public void updateAfterFixedTermDeposit(Accounts account, Double amount) {
        account.updateBalance(amount);
        accountRepository.save(account);
    }

    public Accounts findByCBU(String CBU) {
        return accountRepository.findByCBU(CBU)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    public Double getBalanceInARS(Accounts account) {
        return account.getCurrency() == CurrencyEnum.ARS ? account.getBalance() : 0.0;
    }

    public Double getBalanceInUSD(Accounts account) {
        return account.getCurrency() == CurrencyEnum.USD ? account.getBalance() : 0.0;
    }

    public Accounts getAccountFrom(String token) {
        String accountIdToken = jwtService.getClaimFromToken(token, "accountId");
        Long accountId = Long.parseLong(accountIdToken);
        return accountRepository.findById(accountId).orElseThrow();
    }

    public boolean verificarExistenciaAccount(User user, CurrencyEnum currency, AccountTypeEnum accountType) {
        List<Accounts> cuentas = accountRepository.findByUserId(user);
        return cuentas.stream()
                .anyMatch(cuenta -> cuenta.getCurrency().equals(currency) && cuenta.getAccountType().equals(accountType));
    }

    public AccountsDto accountMapper(Accounts account) {
        AccountsDto accountDto = new AccountsDto();
        accountDto.setId(account.getId());
        accountDto.setCurrency(account.getCurrency());
        accountDto.setAccountType(account.getAccountType());
        accountDto.setTransactionLimit(account.getTransactionLimit());
        accountDto.setBalance(account.getBalance());
        accountDto.setCBU(account.getCBU());
        accountDto.setUserId(account.getUserId().getId());


        return accountDto;
    }

    public AccountsDto updateAccount(Long accountId, Double transactionLimit) {
        try {
            // Buscar la cuenta, lanzando una excepción si no se encuentra
            Accounts account = findById(accountId);

            // Verificar si el límite es mayor al permitido por tipo de cuenta
            if (transactionLimit > account.getCurrency().getTransactionLimit() + 1) {
                throw new LimiteTransaccionExcedidoException("Límite de transacción mayor al permitido");
            }

            // Actualizar el límite de transacción
            account.setTransactionLimit(transactionLimit);

            // Guardar los cambios y convertir la cuenta actualizada en un DTO
            AccountsDto accountDto = accountMapper(accountRepository.save(account));

            return accountDto;
        } catch (CuentaNotFoundException | LimiteTransaccionExcedidoException e) {
            throw e; // Lanzar las excepciones específicas al nivel superior
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar la cuenta", e); // Lanzar una excepción general si ocurre otro tipo de error
        }
    }

    public Accounts findById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new CuentaNotFoundException("Cuenta no encontrada"));
    }

    public Page<Accounts> getAllAccounts(int page) {
        int accountsPerPage = paginationConfig.getUsersPerPage(); // Mostrar de a 10 cuentas por página
        Pageable pageable = PageRequest.of(page, accountsPerPage);
        return accountRepository.findAll(pageable);
    }


    public Boolean hasBalance(Accounts account, Double amount) {
        if (account.getBalance().compareTo(amount) > 0) {
            return true;
        } else {
            throw new InsufficientFundsException("No cuenta con los fondos suficientes para realizar esta operacion ");
        }
    }
}


