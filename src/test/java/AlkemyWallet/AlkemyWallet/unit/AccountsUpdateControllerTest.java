package AlkemyWallet.AlkemyWallet.unit;

import AlkemyWallet.AlkemyWallet.controllers.AccountController;
import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.dtos.AccountsDto;
import AlkemyWallet.AlkemyWallet.enums.CurrencyEnum;
import AlkemyWallet.AlkemyWallet.enums.AccountTypeEnum;
import AlkemyWallet.AlkemyWallet.exceptions.CuentaNotFoundException;
import AlkemyWallet.AlkemyWallet.exceptions.LimiteTransaccionExcedidoException;
import AlkemyWallet.AlkemyWallet.repositories.AccountRepository;
import AlkemyWallet.AlkemyWallet.services.AccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountsUpdateControllerTest {

    @Mock
    private AccountService accountService;


    @InjectMocks
    private AccountController accountController;

        @Test
    public void updateAccountTest_Success() {
        // Datos de prueba
        Long accountId = 1L;
        Double newTransactionLimit = 1000.00;
        // Mock del AccountsDto
        AccountsDto mockAccountsDto = new AccountsDto();
        mockAccountsDto.setId(accountId);
        mockAccountsDto.setTransactionLimit(newTransactionLimit);
        // Mock de la cuenta encontrada:
        Accounts accountInDB = new Accounts();
        accountInDB.setId(accountId);
        accountInDB.setTransactionLimit(mockAccountsDto.getTransactionLimit());

        when(accountService.findById(mockAccountsDto.getId())).thenReturn(accountInDB);
        when(accountService.updateAccount(accountId, newTransactionLimit)).thenReturn(mockAccountsDto);

        //Verificar que la cuenta en la base de datos existe
        assertEquals(accountService.findById(mockAccountsDto.getId()), accountInDB);

        // Llamada al método bajo prueba
        ResponseEntity<?> response = accountController.updateAccount(accountId, newTransactionLimit);

        // Verificar que se devolvió un ResponseEntity con estado OK
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verificar que el cuerpo de la respuesta contiene el objeto de cuenta actualizado
        assertEquals(mockAccountsDto, response.getBody());


    }

    @Test
    public void updateAccountTest_CuentaNotFound() {
        // Datos de prueba
        Long accountId = 1L;
        Double newTransactionLimit = 1000.00;

        // Simular que el servicio lanza una excepción cuando se llama a updateAccount
        when(accountService.updateAccount(accountId, newTransactionLimit))
                .thenThrow(new CuentaNotFoundException("Cuenta no encontrada"));

        // Llamada al método bajo prueba
        ResponseEntity<?> response = accountController.updateAccount(accountId, newTransactionLimit);

        // Verificar que se devolvió un ResponseEntity con estado NOT_FOUND
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        // Verificar que el cuerpo de la respuesta contiene el mensaje de error esperado
        assertEquals("Error al actualizar cuenta: Cuenta no encontrada", response.getBody());
    }

    @Test
    public void updateAccountTest_LimiteExcedido_USD() {
        // Datos de prueba
        Double newTransactionLimit = 1200.00; // Límite excedido para USD

        // Crear un objeto AccountDto con los datos especificados
        AccountsDto accountDto = new AccountsDto();
        accountDto.setId(1L);
        accountDto.setCurrency(CurrencyEnum.USD);
        accountDto.setAccountType(AccountTypeEnum.CAJA_AHORRO);
        accountDto.setTransactionLimit(CurrencyEnum.USD.getTransactionLimit()); // Límite actual
        accountDto.setBalance(5000.00);
        accountDto.setCBU("1234567890123456789012");
        accountDto.setUserId(1L);

        // Simular el servicio de cuenta para lanzar una excepción cuando se llama a updateAccount con un límite de transacción nuevo que excede el permitido para USD
        when(accountService.updateAccount(accountDto.getId(), newTransactionLimit))
                .thenThrow(new LimiteTransaccionExcedidoException("Límite de transacción mayor al permitido"));

        // Llamada al método bajo prueba
        ResponseEntity<?> response = accountController.updateAccount(accountDto.getId(), newTransactionLimit);

        // Verificar que se devolvió un ResponseEntity con estado BAD_REQUEST
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        // Verificar que el cuerpo de la respuesta contiene el mensaje de error esperado
        assertEquals("Error al actualizar cuenta: Límite de transacción mayor al permitido", response.getBody());
    }

    @Test
    public void updateAccountTest_LimiteExcedido_ARS() {
        // Datos de prueba
        Double newTransactionLimit = 350000.00; // Límite excedido para USD

        // Crear un objeto AccountDto con los datos especificados
        AccountsDto accountDto = new AccountsDto();
        accountDto.setId(1L);
        accountDto.setCurrency(CurrencyEnum.ARS);
        accountDto.setAccountType(AccountTypeEnum.CAJA_AHORRO);
        accountDto.setTransactionLimit(CurrencyEnum.ARS.getTransactionLimit()); // Límite actual
        accountDto.setBalance(5000.00);
        accountDto.setCBU("1234567890123456789012");
        accountDto.setUserId(1L);

        // Simular el servicio de cuenta para lanzar una excepción cuando se llama a updateAccount con un límite de transacción nuevo que excede el permitido para USD
        when(accountService.updateAccount(accountDto.getId(), newTransactionLimit))
                .thenThrow(new LimiteTransaccionExcedidoException("Límite de transacción mayor al permitido"));

        // Llamada al método bajo prueba
        ResponseEntity<?> response = accountController.updateAccount(accountDto.getId(), newTransactionLimit);

        // Verificar que se devolvió un ResponseEntity con estado BAD_REQUEST
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        // Verificar que el cuerpo de la respuesta contiene el mensaje de error esperado
        assertEquals("Error al actualizar cuenta: Límite de transacción mayor al permitido", response.getBody());
    }


}