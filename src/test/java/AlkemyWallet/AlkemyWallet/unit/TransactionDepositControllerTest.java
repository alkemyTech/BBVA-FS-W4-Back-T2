package AlkemyWallet.AlkemyWallet.unit;

import AlkemyWallet.AlkemyWallet.controllers.TransactionController;
import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.domain.Transaction;
import AlkemyWallet.AlkemyWallet.domain.factory.TransactionFactory;
import AlkemyWallet.AlkemyWallet.dtos.TransactionDTO;
import AlkemyWallet.AlkemyWallet.enums.TransactionEnum;
import AlkemyWallet.AlkemyWallet.exceptions.NonPositiveAmountException;
import AlkemyWallet.AlkemyWallet.exceptions.UnauthorizedTransactionException;
import AlkemyWallet.AlkemyWallet.repositories.TransactionRepository;
import AlkemyWallet.AlkemyWallet.repositories.UserRepository;
import AlkemyWallet.AlkemyWallet.security.config.JwtConfig;
import AlkemyWallet.AlkemyWallet.services.AccountService;
import AlkemyWallet.AlkemyWallet.services.JwtService;
import AlkemyWallet.AlkemyWallet.services.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class TransactionDepositControllerTest {

    @Mock
    private TransactionService transactionService;

    @Mock
    private AccountService accountService;

    @Mock
    private TransactionFactory transactionFactory;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private JwtConfig jwtConfig;

    @Mock
    private UserRepository userRepository;

    @Mock
    JwtService jwtService;

    @InjectMocks
    private TransactionController transactionController;

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
        mockAccount.setId(1L);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "TOKEN");
        when(jwtService.getTokenFromRequest(request)).thenReturn("TOKEN");
        when(accountService.getAccountFrom("TOKEN")).thenReturn(mockAccount);


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
        ResponseEntity<?> transactionId = transactionController.depositMoney(mockTransaction, request);

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
