package AlkemyWallet.AlkemyWallet.integration;

import AlkemyWallet.AlkemyWallet.domain.Role;
import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.enums.RoleEnum;
import AlkemyWallet.AlkemyWallet.services.UserService;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @BeforeEach
    public void setUp() {


        Role adminRole = new Role();
        adminRole.setName(RoleEnum.ADMIN);

        Role userRole = new Role();
        userRole.setName(RoleEnum.USER);

        User adminUser = new User();
        adminUser.setFirstName("Admin");
        adminUser.setLastName("User");
        adminUser.setUserName("admin@example.com");
        adminUser.setPassword("password");
        adminUser.setCreationDate(LocalDateTime.now());
        adminUser.setUpdateDate(LocalDateTime.now());
        adminUser.setSoftDelete(false);
        adminUser.setDni("12345678");
        adminUser.setBirthDate(LocalDate.of(1990, 1, 1));
        adminUser.setRole(adminRole);

        User regularUser = new User();
        regularUser.setFirstName("Regular");
        regularUser.setLastName("User");
        regularUser.setUserName("user@example.com");
        regularUser.setPassword("password");
        regularUser.setCreationDate(LocalDateTime.now());
        regularUser.setUpdateDate(LocalDateTime.now());
        regularUser.setSoftDelete(false);
        regularUser.setDni("87654321");
        regularUser.setBirthDate(LocalDate.of(1990, 1, 1));
        regularUser.setRole(userRole);

        when(userService.getAllUsers(anyInt())).thenReturn(new PageImpl<>(Arrays.asList(adminUser, regularUser)));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    public void testGetUsersAsAdmin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                        .param("page", "0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users").isArray())
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.totalPages").isNumber());
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = {"USER"})
    public void testGetUsersAsRegularUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                        .param("page", "0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}