package AlkemyWallet.AlkemyWallet.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.domain.Transaction;
import AlkemyWallet.AlkemyWallet.domain.factory.TransactionFactory;
import AlkemyWallet.AlkemyWallet.dtos.TransactionDTO;
import AlkemyWallet.AlkemyWallet.enums.TransactionEnum;
import AlkemyWallet.AlkemyWallet.exceptions.NonPositiveAmountException;
import AlkemyWallet.AlkemyWallet.exceptions.UnauthorizedTransactionException;
import AlkemyWallet.AlkemyWallet.repositories.TransactionRepository;
import AlkemyWallet.AlkemyWallet.services.AccountService;
import AlkemyWallet.AlkemyWallet.services.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class TransactionDepositTest {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private AccountService accountService;

    @Mock
    private TransactionFactory transactionFactory;

    @Mock
    private TransactionRepository transactionRepository;

    //Test para cuando los 2 valores son correctos
    @Test
    void testParaCuandoLosDatosIngresadosSonCorrectos() {
        // Creo un transactionDTo
        TransactionDTO mockTransaction = new TransactionDTO();
        mockTransaction.setDestino("mocked_destination_cbu");
        mockTransaction.setAmount(100.0);
        mockTransaction.setCurrency("USD");

        // Account que viene por parametro
        Accounts mockAccount = new Accounts();
        mockAccount.setCBU("mocked_destination_cbu");

        // Hacemos que el metodo tenga sentido, simulando el servicio
        Accounts mockCuentaDestino = new Accounts();
        mockCuentaDestino.setCBU("mocked_destination_cbu");

        when(accountService.findByCBU(mockTransaction.getDestino())).thenReturn(mockCuentaDestino);

        // Creamos una nueva transaccion
        Transaction mockDepositTransaction = new Transaction();
        mockDepositTransaction.setId(1L);
        mockDepositTransaction.setAmount(mockTransaction.getAmount());
        mockDepositTransaction.setType(TransactionEnum.DEPOSIT);
        mockDepositTransaction.setDescription("");
        mockDepositTransaction.setTransactionDate(LocalDateTime.now());
        mockDepositTransaction.setAccount(mockCuentaDestino); // Cuenta destino
        mockDepositTransaction.setOriginAccount(mockAccount); // Cuenta origen

        when(transactionFactory.createTransaction(
                eq(mockTransaction.getAmount()),
                eq(TransactionEnum.DEPOSIT),
                eq(""),
                any(LocalDateTime.class),
                eq(mockCuentaDestino),
                eq(mockAccount))).thenReturn(mockDepositTransaction);

        // Guardando la transaccion
        when(transactionRepository.save(mockDepositTransaction)).thenReturn(mockDepositTransaction);

        // Performing the test
        Long transactionId = transactionService.depositMoney(mockTransaction, mockAccount);

        // Assertions
        assertNotNull(transactionId);
        assertEquals(1L, transactionId); // Ensure that the returned transaction ID matches the mocked transaction ID
    }

    //Lógica para cuando la cuenta origen y destino son distintas
    @Test
    void testParaCuandoLaCuentaOrigenEsDistintaALaCuentaDestino() {
        // Mocking TransactionDTO
        TransactionDTO mockTransaction = new TransactionDTO();
        mockTransaction.setDestino("mocked_destination_cbu");
        mockTransaction.setAmount(100.0);
        mockTransaction.setCurrency("USD");

        //"Busco" la cuenta destino
        Accounts mockCuentaDestino = new Accounts();
        when(accountService.findByCBU(mockTransaction.getDestino())).thenReturn(mockCuentaDestino);

        // Defino la cuenta origen
        Accounts mockCuentaOrigen = new Accounts();

        // Las cuentas tienen distinto CBU
        mockCuentaDestino.setCBU("mocked_destination_cbu");
        mockCuentaOrigen.setCBU("mocked_origin_cbu");

        Exception exception = assertThrows(UnauthorizedTransactionException.class, () -> {
            transactionService.depositMoney(mockTransaction, mockCuentaOrigen);
        });

        assertEquals("Para realizar un deposito, la cuenta origen debe coincidir con la cuenta destino", exception.getMessage());
    }

    @Test
    void testParaCuandoElAmountEsMenorOIgualA0() {
        // Creo el TransactionDto
        TransactionDTO mockTransaction = new TransactionDTO();
        mockTransaction.setDestino("mocked_destination_cbu");
        mockTransaction.setAmount(0.0); // El amount es cero para que tire la otra exception
        mockTransaction.setCurrency("USD");

        // Hago que las cuentas concuerden
        Accounts mockDestinationAccount = new Accounts();
        mockDestinationAccount.setCBU("mocked_destination_cbu");
        when(accountService.findByCBU(mockTransaction.getDestino())).thenReturn(mockDestinationAccount);

        // Defino la cuenta origen y hago que concuerden con el mismo CBU
        Accounts mockOriginAccount = new Accounts();
        mockOriginAccount.setCBU("mocked_destination_cbu");

        Exception exception = assertThrows(NonPositiveAmountException.class, () -> {
            transactionService.depositMoney(mockTransaction, mockOriginAccount);
        });

        assertEquals("El monto del depósito debe ser mayor que cero", exception.getMessage());
    }
}