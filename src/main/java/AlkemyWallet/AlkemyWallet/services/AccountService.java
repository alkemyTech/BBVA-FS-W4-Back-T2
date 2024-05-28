package AlkemyWallet.AlkemyWallet.services;
import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.dtos.AccountsDto;
import AlkemyWallet.AlkemyWallet.enums.CurrencyEnum;
import AlkemyWallet.AlkemyWallet.repositories.AccountRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;


@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    public final ModelMapper modelMapper;
    private final UserService userService;

    public List<Accounts> findAccountsByUserId(long userId) {
        User user = userService.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return accountRepository.findByUserId(user);
    }


    public Accounts add(CurrencyEnum currency, HttpServletRequest request){

        AccountsDto account = new AccountsDto();

        //Configuro datos que no se pueden inicializar normalmente

        account.setTransactionLimit(currency.getTransactionLimit());
        account.setBalance(0.00);
        account.setCBU(generarCBU());
        account.setUserId(userService.getIdFromRequest(request)); // --> JWT
        account.setCurrency(currency);

        //Termino de rellenar con la Clase Account así se inicializan el resto

        Accounts accountBD = modelMapper.map(account,Accounts.class);

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
        for (int i = 0; i < 22; i++) {
            sum += (Character.getNumericValue(cbu.charAt(i)) * weights[i]) % 10;
        }
        int dv = (10 - (sum % 10)) % 10;
        cbu.setCharAt(7, Character.forDigit(dv, 10));

        return cbu.toString();

    }
    public String generarCBU(){
        String CBU = logicaCBU();
        while(accountRepository.findByCBU(CBU).isEmpty()){
            CBU=logicaCBU();
        }
        return CBU;
    }

    public Accounts findByCBU(String CBU){
        return accountRepository.findByCBU(CBU).orElseThrow();
    }


    public void updateAfterTransaction(Accounts account, Double amount) {
        account.updateBalance(amount);
        account.updateLimit(amount);
    }
}

