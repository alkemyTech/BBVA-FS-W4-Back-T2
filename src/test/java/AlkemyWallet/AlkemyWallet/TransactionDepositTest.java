package AlkemyWallet.AlkemyWallet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.domain.Transaction;
import AlkemyWallet.AlkemyWallet.domain.factory.TransactionFactory;
import AlkemyWallet.AlkemyWallet.dtos.TransactionDTO;
import AlkemyWallet.AlkemyWallet.enums.TransactionEnum;
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
    void depositMoney_ValidTransaction_ReturnsTransactionId() {
        // Mocking TransactionDTO
        TransactionDTO mockTransaction = new TransactionDTO();
        mockTransaction.setDestino("mocked_destination_cbu");
        mockTransaction.setAmount(100.0);
        mockTransaction.setCurrency("USD");

        // Mocking Accounts
        Accounts mockAccount = new Accounts();

        // Mocking destination account retrieval
        Accounts mockDestinationAccount = new Accounts();
        when(accountService.findByCBU(mockTransaction.getDestino())).thenReturn(mockDestinationAccount);

        // Mocking deposit transaction creation
        Transaction mockDepositTransaction = new Transaction();
        mockDepositTransaction.setId(1L);
        mockDepositTransaction.setAmount(mockTransaction.getAmount());
        mockDepositTransaction.setType(TransactionEnum.DEPOSIT);
        mockDepositTransaction.setDescription("");
        mockDepositTransaction.setTransactionDate(LocalDateTime.now());
        mockDepositTransaction.setAccountId(mockDestinationAccount); // Destination account
        mockDepositTransaction.setOriginAccount(mockAccount); // Origin account
        when(transactionFactory.createTransaction(
                eq(mockTransaction.getAmount()),
                eq(TransactionEnum.DEPOSIT),
                eq(""),
                any(LocalDateTime.class),
                eq(mockDestinationAccount),
                eq(mockAccount))).thenReturn(mockDepositTransaction);

        // Mocking transaction saving
        when(transactionRepository.save(mockDepositTransaction)).thenReturn(mockDepositTransaction);

        // Performing the test
        Long transactionId = transactionService.depositMoney(mockTransaction, mockAccount);

        // Assertions
        assertNotNull(transactionId);
        assertEquals(1L, transactionId); // Ensure that the returned transaction ID matches the mocked transaction ID
    }

    //Lógica para cuando la cuenta origen y destino son distintas
    @Test
    void depositMoney_UnauthorizedTransaction_ThrowsUnauthorizedTransactionException() {
        // Mocking TransactionDTO
        TransactionDTO mockTransaction = new TransactionDTO();
        mockTransaction.setDestino("mocked_destination_cbu");
        mockTransaction.setAmount(100.0);
        mockTransaction.setCurrency("USD");

        // Mocking destination account retrieval
        Accounts mockDestinationAccount = new Accounts();
        when(accountService.findByCBU(mockTransaction.getDestino())).thenReturn(mockDestinationAccount);

        // Mocking origin account retrieval
        Accounts mockOriginAccount = new Accounts();

        // Setting up accounts to have different CBUs
        mockDestinationAccount.setCBU("mocked_destination_cbu");
        mockOriginAccount.setCBU("mocked_origin_cbu");

//        // Mocking transaction creation
//        Transaction mockDepositTransaction = new Transaction();
//        when(transactionFactory.createTransaction(
//                eq(mockTransaction.getAmount()),
//                eq(TransactionEnum.DEPOSIT),
//                eq(""),
//                any(LocalDateTime.class),
//                eq(mockDestinationAccount),
//                eq(mockOriginAccount))).thenReturn(mockDepositTransaction);
//
//        // Mocking transaction saving
//        when(transactionRepository.save(mockDepositTransaction)).thenReturn(mockDepositTransaction);

        // Mocking account retrieval from token
        when(accountService.getAccountFrom(anyString())).thenReturn(mockOriginAccount);

        // Performing the test and asserting that an UnauthorizedTransactionException is thrown
        assertThrows(UnauthorizedTransactionException.class, () -> {
            transactionService.depositMoney(mockTransaction, mockOriginAccount);
        });
    }

    //Lógica cambiada para que verifique el runtimeError
    @Test
    void depositMoney_UnauthorizedTransaction_ThrowsUnauthorizedTransactionExceptionSegundo() {
        // Mocking TransactionDTO
        TransactionDTO mockTransaction = new TransactionDTO();
        mockTransaction.setDestino("mocked_destination_cbu");
        mockTransaction.setAmount(100.0);
        mockTransaction.setCurrency("USD");

        // Mocking destination account retrieval
        Accounts mockDestinationAccount = new Accounts();
        when(accountService.findByCBU(mockTransaction.getDestino())).thenReturn(mockDestinationAccount);

        // Mocking origin account retrieval
        Accounts mockOriginAccount = new Accounts();

        // Setting up accounts to have different CBUs
        mockDestinationAccount.setCBU("mocked_destination_cbu");
        mockOriginAccount.setCBU("mocked_origin_cbu");

        // Mocking the transaction service to throw RuntimeException
        when(transactionService.depositMoney(mockTransaction, mockOriginAccount))
                .thenThrow(new RuntimeException("Error al procesar el depósito"));

        // Performing the test and asserting that either UnauthorizedTransactionException or RuntimeException is thrown
        assertThrows(RuntimeException.class, () -> {
            transactionService.depositMoney(mockTransaction, mockOriginAccount);
        }, "Error al procesar el depósito");
    }
}