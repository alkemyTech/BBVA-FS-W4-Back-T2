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
class TransactionDepositServiceTest {

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

        TransactionDTO mockTransaction = new TransactionDTO();
        mockTransaction.setDestino("1234567890123456789012");
        mockTransaction.setAmount(100.0);
        mockTransaction.setCurrency("USD");
        mockTransaction.setDescription("mocked_description");

        Accounts mockAccount = new Accounts();
        mockAccount.setId(1L);
        mockAccount.setCBU("mocked_cbu");

        Accounts mockDestinationAccount = new Accounts();
        mockDestinationAccount.setId(1L);
        mockDestinationAccount.setCBU("mocked_cbu");

        when(accountService.findByCBU(mockTransaction.getDestino())).thenReturn(mockDestinationAccount);

        Transaction expectedTransaction = new Transaction(
                100.0, TransactionEnum.DEPOSIT, "mocked_description", LocalDateTime.now(), mockDestinationAccount, mockAccount
        );
        when(transactionFactory.createTransaction(
                eq(100.0), eq(TransactionEnum.DEPOSIT), eq("mocked_description"), any(LocalDateTime.class), eq(mockDestinationAccount), eq(mockAccount)
        )).thenReturn(expectedTransaction);

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction savedTransaction = invocation.getArgument(0);
            savedTransaction.setId(1L);
            return savedTransaction;
        });

        Long transactionId = transactionService.depositMoney(mockTransaction, mockAccount);

        assertNotNull(transactionId);
        assertEquals(1L, transactionId);
    }


    //Lógica para cuando la cuenta origen y destino son distintas
    @Test
    void testParaCuandoLaCuentaOrigenEsDistintaALaCuentaDestino() {
        // Mocking TransactionDTO
        TransactionDTO mockTransaction = new TransactionDTO();
        mockTransaction.setDestino("1234567890123456789012");
        mockTransaction.setAmount(100.0);
        mockTransaction.setCurrency("USD");
        mockTransaction.setDescription("mocked_description");

        // Mock de las cuentas origen y destino
        Accounts mockCuentaDestino = new Accounts();
        mockCuentaDestino.setCBU("mocked_origin_cbu");
        mockCuentaDestino.setId(1L);

        Accounts mockCuentaOrigen = new Accounts();
        mockCuentaOrigen.setCBU("mocked_destination_cbu");
        mockCuentaOrigen.setId(2L);

        when(accountService.findByCBU(mockTransaction.getDestino())).thenReturn( mockCuentaDestino);

        // Lanzar la excepción esperada
        Exception exception = assertThrows(UnauthorizedTransactionException.class, () -> {
            transactionService.depositMoney(mockTransaction, mockCuentaOrigen);
        });

        // Verificar el mensaje de la excepción
        assertEquals("Para realizar un deposito, la cuenta origen debe coincidir con la cuenta destino", exception.getMessage());
    }


    @Test
    void testParaCuandoElAmountEsMenorOIgualA0() {

        TransactionDTO mockTransaction = new TransactionDTO();
        mockTransaction.setDestino("1234567890123456789012");
        mockTransaction.setAmount(0.0);
        mockTransaction.setCurrency("USD");
        mockTransaction.setDescription("mocked_description");

        Accounts mockAccount = new Accounts();
        mockAccount.setId(1L);
        mockAccount.setCBU("mocked_cbu");

        Accounts mockDestinationAccount = new Accounts();
        mockDestinationAccount.setId(1L);
        mockDestinationAccount.setCBU("mocked_cbu");

        when(accountService.findByCBU(mockTransaction.getDestino())).thenReturn(mockDestinationAccount);


        Exception exception = assertThrows(NonPositiveAmountException.class, () -> {
            transactionService.depositMoney(mockTransaction, mockAccount);
        });

        assertEquals("El monto del depósito debe ser mayor que cero", exception.getMessage());
    }
}