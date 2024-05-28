package AlkemyWallet.AlkemyWallet.services;

import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.dtos.AccountsDto;
import AlkemyWallet.AlkemyWallet.dtos.CurrencyDto;
import AlkemyWallet.AlkemyWallet.enums.CurrencyEnum;
import AlkemyWallet.AlkemyWallet.mappers.ModelMapperConfig;
import AlkemyWallet.AlkemyWallet.repositories.AccountRepository;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import java.util.Random;

@Service
public class AccountService {
    private AccountRepository accountRepository;
    public final ModelMapperConfig modelMapper;

    private final UserService userService;

    public AccountService(ModelMapperConfig modelMapper, UserService userService, AccountRepository accountRepository) {
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.accountRepository = accountRepository;
    }

    public Accounts add(CurrencyDto currency, HttpServletRequest request){

        AccountsDto account = new AccountsDto();
        CurrencyEnum currencyEnum = CurrencyEnum.valueOf(currency.getCurrency());

        //Configuro datos que no se pueden inicializar normalmente

        account.setTransactionLimit(currencyEnum.getTransactionLimit());
        account.setBalance(0.00);
        account.setCBU(generarCBU());
        Long userId = userService.getIdFromRequest(request);
        User user = userService.findById(userId).orElseThrow();
        account.setUserId(user); // --> JWT
        account.setCurrency(currencyEnum);

        //Termino de rellenar con la Clase Account así se inicializan el resto

        Accounts accountBD = modelMapper.modelMapper().map(account,Accounts.class);

        return accountRepository.save(accountBD);
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





}