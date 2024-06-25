package AlkemyWallet.AlkemyWallet.integration;



import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.dtos.TransactionDTO;
import AlkemyWallet.AlkemyWallet.dtos.TransactionResponse;
import AlkemyWallet.AlkemyWallet.enums.CurrencyEnum;
import AlkemyWallet.AlkemyWallet.enums.TransactionEnum;
import AlkemyWallet.AlkemyWallet.exceptions.IncorrectCurrencyException;
import AlkemyWallet.AlkemyWallet.exceptions.InsufficientFundsException;
import AlkemyWallet.AlkemyWallet.services.AccountService;
import AlkemyWallet.AlkemyWallet.services.JwtService;
import AlkemyWallet.AlkemyWallet.services.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.BeforeEach;

import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application.properties")
public class TransactionControllerIntegrationUSDTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private TransactionService transactionService;

    @BeforeEach
    public void setup() {
        // Mock de accountService
        when(accountService.getAccountFrom(any(String.class))).thenReturn(new Accounts());

        // Mock de jwtService
        when(jwtService.getTokenFromRequest(any(HttpServletRequest.class))).thenReturn("dummyToken");
    }

    @Test
    public void testSendUsd_SuccessfulTransaction() throws Exception {
        // Mockear DTO de transacción
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setDestino("1234567890123456789019");
        transactionDTO.setAmount(100.0); // Monto en dólares
        transactionDTO.setCurrency("USD");
        transactionDTO.setDescription("Pago en dólares");

        TransactionResponse transactionResponse = new TransactionResponse();
        transactionResponse.setDestino(transactionDTO.getDestino());
        transactionResponse.setOrigen("1234567890123456789012");
        transactionResponse.setFechaDeTransaccion(LocalDate.now());
        transactionResponse.setTipoDeTransaccion(TransactionEnum.DEPOSIT);
        transactionResponse.setCurrency(transactionDTO.getCurrency());
        transactionResponse.setDescripcion(transactionDTO.getDescription());


        when(transactionService.registrarTransaccion(any(TransactionDTO.class), any(Accounts.class)))
                .thenReturn(transactionResponse);


        mockMvc.perform(MockMvcRequestBuilders.post("/transactions/sendUsd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(transactionDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.destino").value(transactionResponse.getDestino()))
                .andExpect(jsonPath("$.origen").value(transactionResponse.getOrigen()))
                .andExpect(jsonPath("$.fechaDeTransaccion").exists())
                .andExpect(jsonPath("$.tipoDeTransaccion").value(transactionResponse.getTipoDeTransaccion().toString()))
                .andExpect(jsonPath("$.currency").value(transactionResponse.getCurrency()))
                .andExpect(jsonPath("$.descripcion").value(transactionResponse.getDescripcion()));


        verify(transactionService, times(1)).registrarTransaccion(eq(transactionDTO), any(Accounts.class));


    }

    @Test
    public void testSendMoney_InsufficientFunds() throws Exception {

        Accounts mockAccount = new Accounts();
        mockAccount.setId(1L);
        when(accountService.getAccountFrom(any(String.class))).thenReturn(mockAccount);


        when(transactionService.registrarTransaccion(any(TransactionDTO.class), any(Accounts.class)))
                .thenThrow(new InsufficientFundsException("No hay suficientes fondos"));


        TransactionDTO transactionDTO = TransactionDTO.builder()
                .destino("1234567891234567891231")
                .amount(1000.0)
                .currency(CurrencyEnum.USD.name())
                .description("Payment")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/transactions/sendUsd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(transactionDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN));
    }

    @Test
    public void testSendMoney_IncorrectCurrency() throws Exception {

        Accounts mockAccount = new Accounts();
        mockAccount.setId(1L);
        when(accountService.getAccountFrom(any(String.class))).thenReturn(mockAccount);


        when(transactionService.registrarTransaccion(any(TransactionDTO.class), any(Accounts.class)))
                .thenThrow(new IncorrectCurrencyException("La moneda seleccionada no es la correcta"));


        TransactionDTO transactionDTO = TransactionDTO.builder()
                .destino("1234567890123456789012")
                .amount(100.0)
                .currency("ARS")
                .description("Payment")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/transactions/sendUsd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(transactionDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN));
    }

    @Test
    public void testSendMoney_GeneralException() throws Exception {
        // Mocking the account service to return an account
        Accounts mockAccount = new Accounts();
        mockAccount.setId(1L);
        when(accountService.getAccountFrom(any(String.class))).thenReturn(mockAccount);

        // Mocking the transaction service to throw a generic exception
        when(transactionService.registrarTransaccion(any(TransactionDTO.class), any(Accounts.class)))
                .thenThrow(new RuntimeException("Error genérico en la transacción"));

        // Creating a transaction request
        TransactionDTO transactionDTO = TransactionDTO.builder()
                .destino("1234567890123456789012") // Replace with valid CBU
                .amount(500.0)
                .currency(CurrencyEnum.USD.name())
                .description("Payment")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/transactions/sendUsd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(transactionDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN));
    }
}