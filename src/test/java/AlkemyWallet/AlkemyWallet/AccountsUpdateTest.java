package AlkemyWallet.AlkemyWallet;

import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.domain.factory.RoleFactory;
import AlkemyWallet.AlkemyWallet.dtos.AccountRequestDto;
import AlkemyWallet.AlkemyWallet.enums.RoleEnum;
import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.dtos.AccountsDto;
import AlkemyWallet.AlkemyWallet.repositories.AccountRepository;
import AlkemyWallet.AlkemyWallet.repositories.UserRepository;
import AlkemyWallet.AlkemyWallet.services.AccountService;
import AlkemyWallet.AlkemyWallet.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AccountsUpdateTest {

    @Mock
    private AccountRepository accountRepo;

    @InjectMocks
    private AccountService accountService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void AccountUpdateTest(){
        //Datos para servicio y para crear el Dto
        Long accountId = 1L;
        String firstName="Dante";
        String lastName="Zalazar";
        String userName="dante.zalazar@gmail.com";
        String password="1234";
        LocalDate birthDate = LocalDate.of(2000,4,12);

        Double newTransactionLimit = 1000.00;

        //Creo usuario
        User user = new User(
                accountId,
                firstName,
                lastName,
                userName,
                password,
                LocalDateTime.now(),
                LocalDateTime.now(),
                0,
                birthDate,
                "",
                null,
                RoleFactory.getUserRole()
        );
        userRepository.save(user);

        // Inicialización de mocks y inyección de dependencias
        Mockito.when(accountRepo.getReferenceById(accountId)).thenReturn(new Accounts());
        // Puedes necesitar inicializar más mocks aquí dependiendo de cómo se implemente accountService y userService

        //Creo una cuenta
        AccountRequestDto datosCuenta = new AccountRequestDto();
        datosCuenta.setAccountType("CAJA_AHORRO");
        datosCuenta.setCurrency("ARS");
        accountService.addById(datosCuenta,1L);

        Accounts accountDomain = accountRepo.getReferenceById(accountId);
        AccountsDto usuarioEsperado = accountService.accountMapper(accountDomain);
        usuarioEsperado.setTransactionLimit(newTransactionLimit);

        //Test con Mockito
        Mockito.when(accountService.updateAccount(accountId,newTransactionLimit)).thenReturn(usuarioEsperado);

    }



}
