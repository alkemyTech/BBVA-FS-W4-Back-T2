package AlkemyWallet.AlkemyWallet.unit;

import AlkemyWallet.AlkemyWallet.config.CurrencyConfig;
import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.domain.User;
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
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountsUpdateServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void updateAccount_CuentaNotFound() {
        // Datos de prueba
        Long accountId = 1L;
        Double newTransactionLimit = 1000.00;

        // Simular que el repositorio no encuentra la cuenta
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        // Verificar que el servicio lance la excepción adecuada cuando se intenta actualizar la cuenta
        CuentaNotFoundException exception = assertThrows(CuentaNotFoundException.class, () -> {
            accountService.updateAccount(accountId, newTransactionLimit);
        });

        assertEquals("Cuenta inexistente", exception.getMessage());
    }

    @Test
    void updateAccount_LimiteExcedido_USD() {
        // Datos de prueba
        Long accountId = 1L;
        Double newTransactionLimit = 1200.00; // Límite excedido para USD

        // Crear una cuenta simulada con límite actual permitido
        Accounts accountInDB = new Accounts();
        accountInDB.setId(accountId);
        accountInDB.setCurrency(CurrencyEnum.USD);
        accountInDB.setTransactionLimit(CurrencyEnum.USD.getTransactionLimit());

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(accountInDB));

        // Verificar que el servicio lance la excepción adecuada cuando se intenta actualizar con un límite excedido
        LimiteTransaccionExcedidoException exception = assertThrows(LimiteTransaccionExcedidoException.class, () -> {
            accountService.updateAccount(accountId, newTransactionLimit);
        });

        assertEquals("Límite de transacción mayor al permitido", exception.getMessage());
    }

    @Test
    void updateAccount_LimiteExcedido_ARS() {
        // Datos de prueba
        Long accountId = 1L;
        Double newTransactionLimit = 350000.00; // Límite excedido para ARS

        // Crear una cuenta simulada con límite actual permitido
        Accounts accountInDB = new Accounts();
        accountInDB.setId(accountId);
        accountInDB.setCurrency(CurrencyEnum.ARS);
        accountInDB.setTransactionLimit(CurrencyEnum.ARS.getTransactionLimit());

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(accountInDB));

        // Verificar que el servicio lance la excepción adecuada cuando se intenta actualizar con un límite excedido
        LimiteTransaccionExcedidoException exception = assertThrows(LimiteTransaccionExcedidoException.class, () -> {
            accountService.updateAccount(accountId, newTransactionLimit);
        });

        assertEquals("Límite de transacción mayor al permitido", exception.getMessage());
    }

    @Test
    void updateAccount_Success() {
        // Datos de prueba
        Long accountId = 1L;
        Double newTransactionLimit = 800.00; // Dentro del límite permitido
        User user = new User();
        user.setId(1L);

        // Crear una cuenta simulada con límite actual permitido
        Accounts accountInDB = new Accounts();
        accountInDB.setId(accountId);
        accountInDB.setCurrency(CurrencyEnum.USD);
        accountInDB.setTransactionLimit(1000.00);
        accountInDB.setBalance(5000.00);
        accountInDB.setCBU("1234567890123456789012");
        accountInDB.setUserId(user);
        // Mockear el repositorio
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(accountInDB));
        when(accountRepository.save(accountInDB)).thenReturn(accountInDB);

        // Verificar que el servicio actualiza la cuenta correctamente
        AccountsDto updatedAccount = accountService.updateAccount(accountId, newTransactionLimit);

        // Comprobaciones
        assertEquals(accountId, updatedAccount.getId());
        assertEquals(newTransactionLimit, updatedAccount.getTransactionLimit());
    }
}