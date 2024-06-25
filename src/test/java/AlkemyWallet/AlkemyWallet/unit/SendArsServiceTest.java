package AlkemyWallet.AlkemyWallet.unit;


import AlkemyWallet.AlkemyWallet.controllers.TransactionController;
import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.dtos.TransactionDTO;
import AlkemyWallet.AlkemyWallet.exceptions.InsufficientFundsException;
import AlkemyWallet.AlkemyWallet.services.AccountService;
import AlkemyWallet.AlkemyWallet.services.JwtService;
import AlkemyWallet.AlkemyWallet.services.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;


@SpringBootTest
public class SendArsServiceTest {


    @Mock
    private JwtService jwtService;

    @Mock
    private AccountService accountService;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private HttpServletRequest request;
    private String token;
    private String updatedToken;
    private String username;
    private User user;
    private Accounts account;
    private TransactionDTO transactionDTO;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        username = "testUser";
        token = "testToken";
        updatedToken = "updatedToken";
        user = new User();
        user.setId(1L);
        user.setUserName(username);

        account = new Accounts();
        account.setId(1L);
        account.setUserId(user);


        when(jwtService.getTokenFromRequest(request)).thenReturn(token);
        when(jwtService.getUsernameFromToken(token)).thenReturn(username);
        when(accountService.getAccountFrom(token)).thenReturn(account);
        when(jwtService.removeAccountIdFromToken(token)).thenReturn("updatedToken");
    }

    @Test
    public void testSendMoneySuccess() throws Exception {
        transactionDTO = new TransactionDTO();
        transactionDTO.setDestino("1234567899765545");
        transactionDTO.setCurrency("ARS");
        transactionDTO.setAmount(100000.0);



        ResponseEntity<?> response = transactionController.sendMoney(transactionDTO,request);

        assertEquals(HttpStatus.OK,response.getStatusCode());

    }

    @Test
    public void testSendMoneyInsufficientFunds() throws Exception {
        transactionDTO = new TransactionDTO();
        transactionDTO.setDestino("1234567899765545");
        transactionDTO.setCurrency("ARS");
        transactionDTO.setAmount(100000.0);

        when(transactionService.registrarTransaccion(any(TransactionDTO.class), any(Accounts.class)))
                .thenThrow(new InsufficientFundsException("Error: No hay suficientes fondos para completar la transacción"));

        ResponseEntity<?> response = transactionController.sendMoney(transactionDTO,request);

        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());

    }

    @Test
    public void testSendMoneyIncorrectCurrency() throws Exception {
        transactionDTO = new TransactionDTO();
        transactionDTO.setDestino("1234567899765545");
        transactionDTO.setCurrency("USD");
        transactionDTO.setAmount(100000.0);

        when(transactionService.registrarTransaccion(any(TransactionDTO.class), any(Accounts.class)))
                .thenThrow(new InsufficientFundsException("Error: No hay suficientes fondos para completar la transacción"));

        ResponseEntity<?> response = transactionController.sendMoney(transactionDTO,request);

        assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());

    }

    @Test
    public void testSendMoneyGenericException() throws Exception {
        when(transactionService.registrarTransaccion(any(TransactionDTO.class), any(Accounts.class)))
                .thenThrow(new RuntimeException("Error genérico"));

        transactionDTO = new TransactionDTO();
        transactionDTO.setDestino("1234567899765545");
        transactionDTO.setCurrency("ARS");
        transactionDTO.setAmount(100000.0);


        ResponseEntity<?> response = transactionController.sendMoney(transactionDTO,request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,response.getStatusCode());

    }
}