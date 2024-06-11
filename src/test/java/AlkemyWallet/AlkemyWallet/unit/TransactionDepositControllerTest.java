package AlkemyWallet.AlkemyWallet.unit;

import AlkemyWallet.AlkemyWallet.controllers.TransactionController;
import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.dtos.TransactionDTO;
import AlkemyWallet.AlkemyWallet.exceptions.NonPositiveAmountException;
import AlkemyWallet.AlkemyWallet.exceptions.UnauthorizedTransactionException;
import AlkemyWallet.AlkemyWallet.services.AccountService;
import AlkemyWallet.AlkemyWallet.services.JwtService;
import AlkemyWallet.AlkemyWallet.services.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionDepositControllerTest {

    @InjectMocks
    private TransactionController transactionController;

    @Mock
    private AccountService accountService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    @Test
    void depositMoney_Success() {
        // Creo los datos de prueba
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setDestino("mocked_destination_cbu");
        transactionDTO.setAmount(100.0);
        transactionDTO.setCurrency("USD");

        // Mockeo el token y la cuenta
        String mockToken = "mocked_jwt_token";
        when(jwtService.getTokenFromRequest(request)).thenReturn(mockToken);

        Accounts mockAccount = new Accounts();
        when(accountService.getAccountFrom(mockToken)).thenReturn(mockAccount);

        // Mockeo el resultado del servicio de transacciones
        Long mockTransactionId = 1L;
        when(transactionService.depositMoney(transactionDTO, mockAccount)).thenReturn(mockTransactionId);

        // Llamo al método del controlador
        ResponseEntity<?> response = transactionController.depositMoney(transactionDTO, request);

        // Verifico la respuesta
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockTransactionId, response.getBody());
    }

    @Test
    void depositMoney_UnauthorizedTransactionException() {
        // Creo los datos de prueba
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setDestino("mocked_destination_cbu");
        transactionDTO.setAmount(100.0);
        transactionDTO.setCurrency("USD");

        // Mockeo el token y la cuenta
        String mockToken = "mocked_jwt_token";
        when(jwtService.getTokenFromRequest(request)).thenReturn(mockToken);

        Accounts mockAccount = new Accounts();
        when(accountService.getAccountFrom(mockToken)).thenReturn(mockAccount);

        // Mockeo la excepción lanzada por el servicio de transacciones
        doThrow(new UnauthorizedTransactionException("Para realizar un deposito, la cuenta origen debe coincidir con la cuenta destino"))
                .when(transactionService).depositMoney(transactionDTO, mockAccount);

        // Llamo al método del controlador y verifico la excepción
        Exception exception = assertThrows(UnauthorizedTransactionException.class, () -> {
            transactionController.depositMoney(transactionDTO, request);
        });

        // Verifico el mensaje de la excepción
        assertEquals("Para realizar un deposito, la cuenta origen debe coincidir con la cuenta destino", exception.getMessage());
    }

    @Test
    void depositMoney_NonPositiveAmountException() {
        // Creo los datos de prueba
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setDestino("mocked_destination_cbu");
        transactionDTO.setAmount(0.0);
        transactionDTO.setCurrency("USD");

        // Mockeo el token y la cuenta
        String mockToken = "mocked_jwt_token";
        when(jwtService.getTokenFromRequest(request)).thenReturn(mockToken);

        Accounts mockAccount = new Accounts();
        when(accountService.getAccountFrom(mockToken)).thenReturn(mockAccount);

        // Mockeo la excepción lanzada por el servicio de transacciones
        doThrow(new NonPositiveAmountException("El monto del depósito debe ser mayor que cero"))
                .when(transactionService).depositMoney(transactionDTO, mockAccount);

        // Llamo al método del controlador y verifico la excepción
        Exception exception = assertThrows(NonPositiveAmountException.class, () -> {
            transactionController.depositMoney(transactionDTO, request);
        });

        // Verifico el mensaje de la excepción
        assertEquals("El monto del depósito debe ser mayor que cero", exception.getMessage());
    }
}