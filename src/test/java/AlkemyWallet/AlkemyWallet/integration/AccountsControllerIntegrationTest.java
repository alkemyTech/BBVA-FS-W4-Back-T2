package AlkemyWallet.AlkemyWallet.integration;

import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.enums.AccountTypeEnum;
import AlkemyWallet.AlkemyWallet.enums.CurrencyEnum;
import AlkemyWallet.AlkemyWallet.services.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @BeforeEach
    public void setUp() {
        // Mock de accounts para accountService.getAllAccounts(page)
        Accounts account1 = new Accounts(1L, CurrencyEnum.ARS, AccountTypeEnum.CAJA_AHORRO, 100000.0, 50000.0,
                LocalDateTime.now(), LocalDateTime.now(), false, "1234567890123456789012", "Account 1", null);
        Accounts account2 = new Accounts(2L, CurrencyEnum.USD, AccountTypeEnum.CUENTA_CORRIENTE, 200000.0, 150000.0,
                LocalDateTime.now(), LocalDateTime.now(), false, "9876543210987654321098", "Account 2", null);
        when(accountService.getAllAccounts(anyInt()))
                .thenReturn(new PageImpl<>(Arrays.asList(account1, account2)));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    public void testGetAccountsAsAdmin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/accounts")
                        .param("page", "0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accounts").isArray())
                .andExpect(jsonPath("$.accounts.length()").value(2)) // Verificar que hay 2 cuentas en la respuesta
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.totalPages").value(1)); // Suponiendo que hay una página de resultados
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = {"USER"})
    public void testGetAccountsAsRegularUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/accounts")
                        .param("page", "0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}