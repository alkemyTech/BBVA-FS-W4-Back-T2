package AlkemyWallet.AlkemyWallet.unit;

import AlkemyWallet.AlkemyWallet.controllers.AccountController;
import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.dtos.BalanceDTO;
import AlkemyWallet.AlkemyWallet.repositories.UserRepository;
import AlkemyWallet.AlkemyWallet.services.BalanceService;
import AlkemyWallet.AlkemyWallet.services.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class BalanceTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BalanceService balanceService;

    @InjectMocks
    private AccountController balanceController;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetBalanceSuccess() {
        String token = "validToken";
        String username = "testUser";
        User user = new User();
        user.setId(1L);
        user.setUserName(username);

        when(jwtService.getTokenFromRequest(request)).thenReturn(token);
        when(jwtService.getUsernameFromToken(token)).thenReturn(username);
        when(userRepository.findByUserName(username)).thenReturn(Optional.of(user));

        BalanceDTO balanceDTO = new BalanceDTO();
        when(balanceService.getUserBalanceAndTransactions(user.getId())).thenReturn(balanceDTO);

        ResponseEntity<?> response = balanceController.getBalance(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(balanceDTO, response.getBody());
    }

    @Test
    public void testGetBalanceUserNotFound() {
        String token = "validToken";
        String username = "testUser";

        when(jwtService.getTokenFromRequest(request)).thenReturn(token);
        when(jwtService.getUsernameFromToken(token)).thenReturn(username);
        when(userRepository.findByUserName(username)).thenReturn(Optional.empty());

        ResponseEntity<?> response = balanceController.getBalance(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Usuario no encontrado", response.getBody());
    }

    @Test
    public void testGetBalanceTokenExpired() {
        String token = "expiredToken";

        when(jwtService.getTokenFromRequest(request)).thenReturn(token);
        when(jwtService.getUsernameFromToken(token)).thenThrow(ExpiredJwtException.class);

        ResponseEntity<?> response = balanceController.getBalance(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Token ha expirado", response.getBody());
    }

    @Test
    public void testGetBalanceInternalServerError() {
        String token = "validToken";
        String username = "testUser";

        when(jwtService.getTokenFromRequest(request)).thenReturn(token);
        when(jwtService.getUsernameFromToken(token)).thenReturn(username);
        when(userRepository.findByUserName(username)).thenThrow(RuntimeException.class);

        ResponseEntity<?> response = balanceController.getBalance(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error al procesar la solicitud", response.getBody());
    }
}


