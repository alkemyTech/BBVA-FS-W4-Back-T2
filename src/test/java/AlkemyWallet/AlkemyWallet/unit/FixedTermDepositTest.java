package AlkemyWallet.AlkemyWallet.unit;

import AlkemyWallet.AlkemyWallet.controllers.FixedTermDepositController;
import AlkemyWallet.AlkemyWallet.dtos.FixedTermDepositDto;
import AlkemyWallet.AlkemyWallet.exceptions.AccountNotFoundException;
import AlkemyWallet.AlkemyWallet.exceptions.InvalidDateOrderException;
import AlkemyWallet.AlkemyWallet.services.AccountService;
import AlkemyWallet.AlkemyWallet.services.FixedTermDepositService;
import AlkemyWallet.AlkemyWallet.services.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class FixedTermDepositTest {

    @Mock
    private FixedTermDepositService fixedTermDepositService;

    @Mock
    private JwtService jwtService;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private FixedTermDepositController fixedTermDepositController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateFixedTermDepositSuccess() {
        // Arrange
        FixedTermDepositDto fixedTermDepositDto = new FixedTermDepositDto();
        fixedTermDepositDto.setCreationDate("01/01/2024");
        fixedTermDepositDto.setClosingDate("31/01/2024");
        fixedTermDepositDto.setInvertedAmount(1000.0);

        when(fixedTermDepositService.fixedTermDeposit(any(FixedTermDepositDto.class), any(), any()))
                .thenReturn(fixedTermDepositDto);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid_token");

        // Act
        ResponseEntity<?> response = fixedTermDepositController.createFixedTermDeposit(fixedTermDepositDto, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(fixedTermDepositDto, response.getBody());
    }

    @Test
    public void testCreateFixedTermDepositInvalidDates() {
        // Arrange
        FixedTermDepositDto fixedTermDepositDto = new FixedTermDepositDto();
        fixedTermDepositDto.setCreationDate("01/02/2024");
        fixedTermDepositDto.setClosingDate("01/01/2024");
        fixedTermDepositDto.setInvertedAmount(1000.0);

        when(fixedTermDepositService.fixedTermDeposit(any(FixedTermDepositDto.class), any(), any()))
                .thenThrow(new InvalidDateOrderException("La fecha final no puede ser mayor que la fecha inicial"));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid_token");

        // Act & Assert
        assertThrows(InvalidDateOrderException.class, () -> {
            fixedTermDepositController.createFixedTermDeposit(fixedTermDepositDto, request);
        });
    }

    @Test
    public void testCreateFixedTermDepositInsufficientPermissions() {
        // Arrange
        FixedTermDepositDto fixedTermDepositDto = new FixedTermDepositDto();
        fixedTermDepositDto.setCreationDate("01/01/2024");
        fixedTermDepositDto.setClosingDate("31/01/2024");
        fixedTermDepositDto.setInvertedAmount(1000.0);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid_token");

        when(jwtService.getUserFromToken(anyString())).thenThrow(new RuntimeException("Token inválido"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            fixedTermDepositController.createFixedTermDeposit(fixedTermDepositDto, request);
        });
    }

    @Test
    public void testCreateFixedTermDepositAccountNotFound() {
        // Arrange
        FixedTermDepositDto fixedTermDepositDto = new FixedTermDepositDto();
        fixedTermDepositDto.setCreationDate("01/01/2024");
        fixedTermDepositDto.setClosingDate("31/01/2024");
        fixedTermDepositDto.setInvertedAmount(1000.0);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid_token");

        when(jwtService.getTokenFromRequest(request)).thenReturn("valid_token");
        when(accountService.getAccountFrom(anyString())).thenThrow(new AccountNotFoundException("Cuenta no encontrada"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            fixedTermDepositController.createFixedTermDeposit(fixedTermDepositDto, request);
        });
    }
}