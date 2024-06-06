package AlkemyWallet.AlkemyWallet;

import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.dtos.AccountsDto;
import AlkemyWallet.AlkemyWallet.repositories.AccountRepository;
import AlkemyWallet.AlkemyWallet.services.AccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AccountsUpdateTest {

    @Mock
    private AccountRepository accountRepo;

    @InjectMocks
    private AccountService accountService;

    @Test
    public void AccountUpdateTest(){
        //Datos para servicio y para crear el Dto
        Long accountId = 1L;
        Double newTransactionLimit = 1000.00;

        //Agarro el usuario a testear
        Accounts accountDomain = accountRepo.getReferenceById(accountId);
        AccountsDto usuarioEsperado = accountService.accountMapper(accountDomain);
        usuarioEsperado.setTransactionLimit(newTransactionLimit);

        //Test con Mockito
        Mockito.when(accountService.updateAccount(accountId,newTransactionLimit)).thenReturn(usuarioEsperado);


    }



}
