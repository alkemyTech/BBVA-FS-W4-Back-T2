package AlkemyWallet.AlkemyWallet.unit;

import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.domain.Transaction;
import AlkemyWallet.AlkemyWallet.domain.factory.TransactionFactory;
import AlkemyWallet.AlkemyWallet.dtos.TransactionDTO;
import AlkemyWallet.AlkemyWallet.dtos.TransactionResponse;
import AlkemyWallet.AlkemyWallet.enums.CurrencyEnum;
import AlkemyWallet.AlkemyWallet.enums.TransactionEnum;
import AlkemyWallet.AlkemyWallet.exceptions.IncorrectCurrencyException;
import AlkemyWallet.AlkemyWallet.exceptions.InsufficientFundsException;
import AlkemyWallet.AlkemyWallet.mappers.TransactionResponseMapper;
import AlkemyWallet.AlkemyWallet.repositories.TransactionRepository;
import AlkemyWallet.AlkemyWallet.services.AccountService;
import AlkemyWallet.AlkemyWallet.services.TransactionService;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class SendArsTest {


    @Mock
    private AccountService accountService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionFactory transactionFactory;

    @Mock
    private TransactionResponseMapper transactionResponseMapper;

    @InjectMocks
    private TransactionService transactionService;

    private Accounts originAccount;
    private Accounts destinationAccount;


    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        originAccount = new Accounts();
        originAccount.setCurrency(CurrencyEnum.ARS);
        originAccount.setBalance(500.0);
        originAccount.setTransactionLimit(500.0);

        destinationAccount = new Accounts();
        destinationAccount.setCurrency(CurrencyEnum.ARS);
        destinationAccount.setBalance(500.0);
        destinationAccount.setTransactionLimit(500.0);
        destinationAccount.setCBU("123456789");

    }

    @Test
    public void testRegistrarTransaccion() {
        // Datos de prueba
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setAmount(100.0);
        transactionDTO.setCurrency("ARS");
        transactionDTO.setDestino("123456789");


        when(accountService.findByCBU(transactionDTO.getDestino())).thenReturn(destinationAccount);

        Transaction expectedTransaction = new Transaction(
                100.0, TransactionEnum.PAYMENT, "", LocalDateTime.now(), destinationAccount, originAccount
        );
        when(transactionFactory.createTransaction(
                anyDouble(), any(TransactionEnum.class), anyString(), any(LocalDateTime.class), any(Accounts.class), any(Accounts.class)
        )).thenReturn(expectedTransaction);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(expectedTransaction);

        TransactionResponse expectedResponse = new TransactionResponse();
        when(transactionResponseMapper.mapToTransactionResponse(any(Transaction.class), any(Accounts.class), any(Accounts.class)))
                .thenReturn(expectedResponse);

        // Llamada al método a probar
        TransactionResponse actualResponse = transactionService.registrarTransaccion(transactionDTO, originAccount);

        // Verificaciones
        verify(accountService).findByCBU(transactionDTO.getDestino());
        verify(transactionRepository, times(2)).save(any(Transaction.class));
        verify(accountService).updateAfterTransaction(originAccount, 100.0);
        verify(accountService).updateAfterTransaction(destinationAccount, -100.0);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testMonedaInvalida() {
        // Datos de prueba
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setAmount(100.0);
        transactionDTO.setCurrency("USD");
        transactionDTO.setDestino("123456789");


        // Verificar que se lanza IncorrectCurrencyException
        IncorrectCurrencyException thrownCurrencyException = assertThrows(
                IncorrectCurrencyException.class,
                () -> transactionService.registrarTransaccion(transactionDTO, originAccount)
        );

        assertEquals("La moneda seleccionada no es la correcta para este tipo de cuenta", thrownCurrencyException.getMessage());
    }

    @Test
    public void testFondosInsuficientes() {
        // Datos de prueba
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setAmount(1000.0);
        transactionDTO.setCurrency("ARS");
        transactionDTO.setDestino("123456789");


        when(accountService.findByCBU(transactionDTO.getDestino())).thenReturn(destinationAccount);


        // Verificar que se lanza InsufficientFundsException
        InsufficientFundsException thrownFundsException = assertThrows(
                InsufficientFundsException.class,
                () -> transactionService.registrarTransaccion(transactionDTO, originAccount)
        );

        assertEquals("No hay suficiente dinero o límite disponible para completar la transacción", thrownFundsException.getMessage());
    }


}
