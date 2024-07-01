package AlkemyWallet.AlkemyWallet.integration;
import AlkemyWallet.AlkemyWallet.controllers.AuthController;
import AlkemyWallet.AlkemyWallet.dtos.RegisterRequest;
import AlkemyWallet.AlkemyWallet.dtos.RegisterResponse;
import AlkemyWallet.AlkemyWallet.services.AuthenticationService;
import AlkemyWallet.AlkemyWallet.services.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @Autowired
    private JwtService jwtService;

    @Test
    public void testRegisterUser() throws Exception {
        // Crear objeto de solicitud de registro
        RegisterRequest registerRequest = RegisterRequest.builder()
                .userName("testUser@example.com")
                .password("password")
                .firstName("John")
                .lastName("Doe")
                .birthDate("1990-01-01")
                .dni("12345678")
                .build();

        // Mockear el servicio de autenticación para devolver un objeto RegisterResponse simulado
        RegisterResponse mockResponse = RegisterResponse.builder()
                .userName(registerRequest.getUserName())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .id(1L)
                .imagePath("path/to/image")
                .birthDay(registerRequest.getBirthDate())
                .build();
        when(authenticationService.register(any(RegisterRequest.class))).thenReturn(mockResponse);

        // Realizar la solicitud POST al endpoint /register
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userName").value(registerRequest.getUserName()))
                .andExpect(jsonPath("$.firstName").value(registerRequest.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(registerRequest.getLastName()))
                .andExpect(jsonPath("$.id").value(1)) // Suponiendo que el id devuelto es 1
                .andExpect(jsonPath("$.birthDay").value(registerRequest.getBirthDate()))
                .andReturn();

        // Obtener el token del resultado de la solicitud
        String token = result.getResponse().getHeader(HttpHeaders.AUTHORIZATION);
        assertNotNull(token);
        assertTrue(token.startsWith("Bearer "));

        // Verificar que el token sea válido utilizando JwtService
        Claims claims = jwtService.getAllClaims(token.substring(7)); // Remover "Bearer "
        assertEquals(registerRequest.getUserName(), claims.getSubject());
        // Puedes verificar otros claims si es necesario
    }

    @Test
    public void testRegisterUser_InvalidData() throws Exception {
        // Crear objeto de solicitud de registro con datos inválidos
        RegisterRequest invalidRequest = RegisterRequest.builder()
                .userName("invalid-email") // Email inválido
                .password("password")
                .firstName("John")
                .lastName("Doe")
                .birthDate("1990-01-01")
                .dni("12345678")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN));
    }

    @Test
    public void testRegisterUser_RuntimeException() throws Exception {
        when(authenticationService.register(any(RegisterRequest.class))).thenThrow(new RuntimeException("Error interno"));

        RegisterRequest validRequest = RegisterRequest.builder()
                .userName("testUser@example.com")
                .password("password")
                .firstName("John")
                .lastName("Doe")
                .birthDate("1990-01-01")
                .dni("12345678")
                .build();

        // Realizar la solicitud POST al endpoint /register con datos válidos pero con RuntimeException
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(validRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN));
    }
}