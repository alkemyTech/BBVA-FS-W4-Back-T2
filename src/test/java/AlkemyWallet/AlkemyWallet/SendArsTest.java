package AlkemyWallet.AlkemyWallet;

import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.domain.Transaction;
import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.domain.factory.TransactionFactory;
import AlkemyWallet.AlkemyWallet.dtos.TransactionDTO;
import AlkemyWallet.AlkemyWallet.dtos.TransactionResponse;
import AlkemyWallet.AlkemyWallet.enums.AccountTypeEnum;
import AlkemyWallet.AlkemyWallet.enums.CurrencyEnum;
import AlkemyWallet.AlkemyWallet.enums.TransactionEnum;
import AlkemyWallet.AlkemyWallet.exceptions.IncorrectCurrencyException;
import AlkemyWallet.AlkemyWallet.exceptions.InsufficientFundsException;
import AlkemyWallet.AlkemyWallet.mappers.TransactionResponseMapper;
import AlkemyWallet.AlkemyWallet.repositories.AccountRepository;
import AlkemyWallet.AlkemyWallet.repositories.TransactionRepository;
import AlkemyWallet.AlkemyWallet.services.AccountService;
import AlkemyWallet.AlkemyWallet.services.TransactionService;
import jakarta.persistence.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;


import java.time.LocalDateTime;
import java.util.Optional;


@RunWith(SpringRunner.class)
@SpringBootTest
public class SendArsTest {

        @InjectMocks
        private TransactionService transactionService;

        @Mock
        private AccountService accountService;

        @Mock
        private AccountRepository accountRepository;

        @Mock
        private TransactionFactory transactionFactory;

        @Mock
        private TransactionRepository transactionRepository;

        @Mock
        private TransactionResponseMapper transactionResponseMapper;

        private TransactionDTO transactionDTO;
        private Accounts originAccount;

        private Accounts destinationAccount;
        private User user;

        @Before
        public void setUp() {
            MockitoAnnotations.initMocks(this);

            transactionDTO = new TransactionDTO();
            transactionDTO.setDestino("1234567890123456789012");
            transactionDTO.setAmount(100.0);
            transactionDTO.setCurrency("ARS");

            originAccount = mock(Accounts.class);
            user = mock(User.class);


            when(originAccount.getCurrency()).thenReturn(CurrencyEnum.ARS);
            when(originAccount.dineroDisponible(transactionDTO.getAmount())).thenReturn(true);
            when(originAccount.limiteDisponible(transactionDTO.getAmount())).thenReturn(true);

        }

    @Test
    public void testRegistrarTransaccion() {
        // Arrange
        Transaction transaction = new Transaction(1L, 100.0, TransactionEnum.PAYMENT, "", LocalDateTime.now(), destinationAccount, originAccount);
        destinationAccount = new Accounts(1L, CurrencyEnum.ARS,AccountTypeEnum.CAJA_AHORRO, 20000D, 1000D,LocalDateTime.now(),LocalDateTime.now(),false,"1234567890123456789012",user);

        // Crear la instancia de TransactionResponse esperada
        TransactionResponse expectedResponse = new TransactionResponse();

        // Configurar los mocks
        when(accountService.findByCBU(transactionDTO.getDestino())).thenReturn(destinationAccount);
        when(accountRepository.findByCBU(transactionDTO.getDestino())).thenReturn(Optional.ofNullable(destinationAccount));
        when(transactionFactory.createTransaction(any(Double.class), any(TransactionEnum.class), any(String.class), any(LocalDateTime.class), any(Accounts.class), any(Accounts.class))).thenReturn(transaction);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        when(transactionResponseMapper.mapToTransactionResponse(any(Transaction.class), any(Accounts.class), any(Accounts.class))).thenReturn(expectedResponse);

        // Act
        TransactionResponse response = transactionService.registrarTransaccion(transactionDTO, originAccount);

        // Assert
        assertNotNull(response);
        assertEquals(expectedResponse, response); // Asegúrate de que la respuesta es la esperada
        verify(accountService, times(1)).findByCBU(transactionDTO.getDestino());
        verify(originAccount, times(1)).dineroDisponible(transactionDTO.getAmount());
        verify(originAccount, times(1)).limiteDisponible(transactionDTO.getAmount());
        verify(transactionFactory, times(1)).createTransaction(any(Double.class), any(TransactionEnum.class), any(String.class), any(LocalDateTime.class), any(Accounts.class), any(Accounts.class));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(transactionResponseMapper, times(1)).mapToTransactionResponse(any(Transaction.class), any(Accounts.class), any(Accounts.class));
    }
        @Test
        public void testRegistrarTransaccion_IncorrectCurrency() {
            destinationAccount = new Accounts(1L, CurrencyEnum.ARS,AccountTypeEnum.CAJA_AHORRO, 20000D, 1000D,LocalDateTime.now(),LocalDateTime.now(),false,"1234567890123456789012",user);
            // Arrange
            transactionDTO.setCurrency("USD");

            // Act & Assert
            Exception exception = assertThrows(IncorrectCurrencyException.class, () -> {
                transactionService.registrarTransaccion(transactionDTO, originAccount);
            });

            String expectedMessage = "La moneda seleccionada no es la correcta para este tipo de cuenta";
            String actualMessage = exception.getMessage();

            assertTrue(actualMessage.contains(expectedMessage));
        }

        @Test
        public void testRegistrarTransaccion_InsufficientFunds() {
            destinationAccount = new Accounts(1L, CurrencyEnum.ARS,AccountTypeEnum.CAJA_AHORRO, 20000D, 1000D,LocalDateTime.now(),LocalDateTime.now(),false,"1234567890123456789012",user);
            // Arrange
            when(accountService.findByCBU(transactionDTO.getDestino())).thenReturn(destinationAccount);
            when(originAccount.dineroDisponible(transactionDTO.getAmount())).thenReturn(false);


            // Act & Assert
            Exception exception = assertThrows(InsufficientFundsException.class, () -> {
                transactionService.registrarTransaccion(transactionDTO, originAccount);
            });

            String expectedMessage = "No hay suficiente dinero o límite disponible para completar la transacción";
            String actualMessage = exception.getMessage();

            assertTrue(actualMessage.contains(expectedMessage));
        }
}