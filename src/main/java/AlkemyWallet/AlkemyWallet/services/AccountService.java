package AlkemyWallet.AlkemyWallet.services;
import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.domain.FixedTermDeposit;
import AlkemyWallet.AlkemyWallet.domain.Transaction;
import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.dtos.*;
import AlkemyWallet.AlkemyWallet.enums.CurrencyEnum;
import AlkemyWallet.AlkemyWallet.mappers.ModelMapperConfig;
import AlkemyWallet.AlkemyWallet.repositories.AccountRepository;
import AlkemyWallet.AlkemyWallet.repositories.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;

import java.util.*;

@Service
@AllArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    public final ModelMapperConfig modelMapper;
    private final UserService userService;
    private final TransactionRepository transactionRepository;
    private final JwtService jwtService;
    private final FixedTermDepositService fixedTermDepositService;


    public Accounts add(CurrencyDto currency, HttpServletRequest request) {
        try {
            AccountsDto account = new AccountsDto();
            CurrencyEnum currencyEnum = CurrencyEnum.valueOf(currency.getCurrency());

            // Configuro datos que no se pueden inicializar normalmente
            account.setTransactionLimit(currencyEnum.getTransactionLimit());
            account.setBalance(0.00);
            account.setCBU(generarCBU());
            Long userId = userService.getIdFromRequest(request);
            User user = userService.findById(userId).orElseThrow();
            account.setUserId(user); // --> JWT
            account.setCurrency(currencyEnum);

            // Termino de rellenar con la Clase Account así se inicializan el resto
            Accounts accountBD = modelMapper.modelMapper().map(account, Accounts.class);
            Accounts savedAccount = accountRepository.save(accountBD);

            // Add account ID to existing JWT token
            String token = jwtService.getTokenFromRequest(request);
            if (token != null) {
                token = jwtService.addAccountIdToToken(token, String.valueOf(savedAccount.getId()));
            }

            // Devolver la cuenta guardada
            return savedAccount;
        } catch (Exception e) {
            throw new RuntimeException("Error al agregar la cuenta", e);
        }
    }


    public List<Accounts> findAccountsByUserId(long userId) {
        try {
            User user = userService.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            return accountRepository.findByUserId(user);
        } catch (Exception e) {
            throw new RuntimeException("No se encontró al usuario", e);
        }
    }

    public Accounts addById(CurrencyEnum currencyEnum, Long id){

        try{
            AccountsDto account = new AccountsDto();

            //Configuro datos que no se pueden inicializar normalmente

            account.setTransactionLimit(currencyEnum.getTransactionLimit());
            account.setBalance(0.00);
            account.setCBU(generarCBU());
            User user = userService.findById(id).orElseThrow();
            account.setUserId(user); // --> JWT
            account.setCurrency(currencyEnum);

            //Termino de rellenar con la Clase Account así se inicializan el resto

            Accounts accountBD = modelMapper.modelMapper().map(account,Accounts.class);

            return accountRepository.save(accountBD);
        }catch (Exception e){
            throw new RuntimeException("No se pudo añadir la cuenta al usuario",e);
        }

    }

    public static String logicaCBU() {
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
    public String generarCBU() {
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

        // Devuelve el CBU generado y único
        return CBU;
    }

    public BalanceDTO getUserBalanceAndTransactions(Long userId) {
        // Obtener todas las cuentas del usuario
        List<Accounts> accounts = findAccountsByUserId(userId);

        List<TransactionBalanceDTO> transactionsDtos = new ArrayList<>();


        // Calcular el balance total en ARS y USD
        Double totalArsBalance = 0.0;
        Double totalUsdBalance = 0.0;

        for (Accounts account : accounts) {
            Long accountId = account.getId();


            // Calcular el balance total
            if (account.getCurrency().equals(CurrencyEnum.ARS)) {
                totalArsBalance += getBalanceInARS(account);
            } else if (account.getCurrency().equals(CurrencyEnum.USD)) {
                totalUsdBalance += getBalanceInUSD(account);
            }

            List<Transaction> accountTransactions = transactionRepository.findByAccountId(accountId);

            for (Transaction transaction : accountTransactions) {
                TransactionBalanceDTO dto = new TransactionBalanceDTO();
                dto.setId(transaction.getId());
                dto.setAmount(transaction.getAmount());
                dto.setTransactionDate(transaction.getTransactionDate());
                dto.setDescription(transaction.getDescription());
                dto.setType(transaction.getType());
                dto.setCurrency(transaction.getOriginAccount().getCurrency().toString());
                dto.setOriginAccountCBU(transaction.getOriginAccount().getCBU());
                transactionsDtos.add(dto);
            }

        }

        // Obtener los plazos fijos del usuario
        List<FixedTermDeposit> fixedTermDeposits = fixedTermDepositService.getFixedTermDepositsByUser(userId);


        // Crear DTO de respuesta
        BalanceDTO balanceDTO = new BalanceDTO();
        balanceDTO.setAccountArs(totalArsBalance);
        balanceDTO.setAccountUsd(totalUsdBalance);
        balanceDTO.setFixedTerms(fixedTermDeposits);
        balanceDTO.setAccountTransactions(transactionsDtos);
        return balanceDTO;
    }


    public void updateAfterTransaction(Accounts account, Double amount) {
        account.updateBalance(amount);
        account.updateLimit(amount);
        accountRepository.save(account);
    }
    public Accounts findByCBU(String CBU){
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
        String accountIdToken = jwtService.getClaimFromToken(token,"accountId");
        Long accountId = Long.parseLong(accountIdToken);
        return accountRepository.findById(accountId).orElseThrow();
    }

    public Accounts findById(Long id) {
        return accountRepository.findById(id).orElseThrow();
    };
}


