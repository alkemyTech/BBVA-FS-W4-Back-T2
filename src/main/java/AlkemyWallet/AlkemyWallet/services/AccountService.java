package AlkemyWallet.AlkemyWallet.services;
import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.dtos.AccountTypeDto;
import AlkemyWallet.AlkemyWallet.dtos.AccountsDto;
import AlkemyWallet.AlkemyWallet.dtos.CurrencyDto;
import AlkemyWallet.AlkemyWallet.enums.AccountTypeEnum;
import AlkemyWallet.AlkemyWallet.enums.CurrencyEnum;
import AlkemyWallet.AlkemyWallet.mappers.ModelMapperConfig;
import AlkemyWallet.AlkemyWallet.repositories.AccountRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import java.util.List;

@Service
@AllArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    public final ModelMapperConfig modelMapper;
    private final UserService userService;
    private final JwtService jwtService;


    @Autowired
    public AccountService(JwtService jwtService, UserService userService, ModelMapperConfig modelMapper, AccountRepository accountRepository) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.accountRepository = accountRepository;
    }

    public Accounts add(CurrencyDto currency, AccountTypeDto accountType, HttpServletRequest request){
        try {

            Long userId = userService.getIdFromRequest(request);

            User user = userService.findById(userId).orElseThrow();
            CurrencyEnum currencyEnum = CurrencyEnum.valueOf(currency.getCurrency());
            AccountTypeEnum accountTypeEnum = AccountTypeEnum.valueOf(accountType.getName());

//            if(!verificarExistenciaAccount(user,currencyEnum,accountTypeEnum)){
                AccountsDto account = new AccountsDto();
                account.setUserId(user); // --> JWT

                //Configuro datos que no se pueden inicializar normalmente

                account.setTransactionLimit(currencyEnum.getTransactionLimit());
                account.setBalance(0.00);
                account.setCBU(generarCBU());


                account.setCurrency(currencyEnum);
                account.setAccountType(accountTypeEnum);


                //Termino de rellenar con la Clase Account así se inicializan el resto

                Accounts accountBD = modelMapper.modelMapper().map(account,Accounts.class);

                //Programar validación...
                return accountRepository.save(accountBD);
//            }else{
//                throw new RuntimeException("La cuenta con la moneda " + currencyEnum + " y el tipo de cuenta " + accountTypeEnum + " ya existe.");
//            }



        } catch (Exception e) {
            throw new RuntimeException("Error al agregar la cuenta", e);
        }
    }


    public List<Accounts> findAccountsByUserId(long userId) {
        try{
            User user = userService.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            return accountRepository.findByUserId(user);
        }catch (Exception e){
            throw new RuntimeException("No se encontró al usuario",e);
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



    public void updateAfterTransaction(Accounts account, Double amount) {
        account.updateBalance(amount);
        account.updateLimit(amount);
    }
    public Accounts findByCBU(String CBU){
        return accountRepository.findByCBU(CBU)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    public Accounts getAccountFrom(String token) {
        String accountIdToken = jwtService.getClaimFromToken(token,"accountId");
        Long accountId = Long.parseLong(accountIdToken);
        return accountRepository.findById(accountId).orElseThrow();
    }

    public Accounts findById(Long id) {
        return accountRepository.findById(id).orElseThrow();
    };

    public boolean verificarExistenciaAccount(User user,CurrencyEnum currency, AccountTypeEnum accountType){
        List<Accounts> cuentas = findAccountsByUserId(user.getId());
        return cuentas.stream()
                .anyMatch(cuenta -> cuenta.getCurrency().equals(currency) && cuenta.getAccountType().equals(accountType));
    }
}


