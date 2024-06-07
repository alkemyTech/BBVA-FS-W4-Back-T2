package AlkemyWallet.AlkemyWallet;

import AlkemyWallet.AlkemyWallet.controllers.AccountController;
import AlkemyWallet.AlkemyWallet.dtos.AccountsDto;
import AlkemyWallet.AlkemyWallet.exceptions.CuentaNotFoundException;
import AlkemyWallet.AlkemyWallet.exceptions.LimiteTransaccionExcedidoException;
import AlkemyWallet.AlkemyWallet.services.AccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountsUpdateTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    @Test
    public void updateAccountTest_Success() {
        // Datos de prueba
        Long accountId = 1L;
        Double newTransactionLimit = 1000.00;

        // Mock del servicio de cuenta
        AccountsDto mockAccountsDto = new AccountsDto();
        mockAccountsDto.setId(accountId);
        mockAccountsDto.setTransactionLimit(newTransactionLimit);

        when(accountService.updateAccount(accountId, newTransactionLimit)).thenReturn(mockAccountsDto);

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

        // Simular el servicio de cuenta para lanzar una excepción cuando se llama a updateAccount con un ID que no se puede encontrar
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
    public void updateAccountTest_LimiteExcedido() {
        // Datos de prueba
        Long accountId = 1L;
        Double newTransactionLimit = 350000.00;

        // Simular el servicio de cuenta para lanzar una excepción cuando se llama a updateAccount con un límite de transacción nuevo que excede el permitido
        when(accountService.updateAccount(accountId, newTransactionLimit))
                .thenThrow(new LimiteTransaccionExcedidoException("Límite de transacción mayor al permitido"));

        // Llamada al método bajo prueba
        ResponseEntity<?> response = accountController.updateAccount(accountId, newTransactionLimit);

        // Verificar que se devolvió un ResponseEntity con estado BAD_REQUEST
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        // Verificar que el cuerpo de la respuesta contiene el mensaje de error esperado
        assertEquals("Error al actualizar cuenta: Límite de transacción mayor al permitido", response.getBody());
    }
}