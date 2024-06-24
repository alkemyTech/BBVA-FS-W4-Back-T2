package AlkemyWallet.AlkemyWallet.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.dtos.AccountsDto;
import AlkemyWallet.AlkemyWallet.enums.AccountTypeEnum;
import AlkemyWallet.AlkemyWallet.enums.CurrencyEnum;
import AlkemyWallet.AlkemyWallet.exceptions.DuplicateAccountException;
import AlkemyWallet.AlkemyWallet.exceptions.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

import AlkemyWallet.AlkemyWallet.dtos.AccountRequestDto;
import AlkemyWallet.AlkemyWallet.repositories.AccountRepository;
import AlkemyWallet.AlkemyWallet.services.AccountService;
import AlkemyWallet.AlkemyWallet.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private AccountService accountService;

    @Test
    void testCreateAccountWithValidData() {

        AccountRequestDto accountRequestDto = new AccountRequestDto("ARS", "CAJA_AHORRO");
        HttpServletRequest request = mock(HttpServletRequest.class);
        Long userId = 1L;
        User mockUser = new User();
        mockUser.setId(userId);

        Accounts mockAccount = new Accounts
                (1L,CurrencyEnum.ARS,AccountTypeEnum.CAJA_AHORRO,100000D,
                        0D, LocalDateTime.now(),LocalDateTime.now(),false,"1111111111111111111111","alias",mockUser);

        when(userService.getIdFromRequest(request)).thenReturn(userId);
        when(userService.findById(userId)).thenReturn(Optional.of(mockUser));
        when(accountRepository.save(any(Accounts.class))).thenReturn(mockAccount);

        AccountsDto createdAccount = accountService.add(accountRequestDto, request);

        assertNotNull(createdAccount);
        assertEquals(CurrencyEnum.ARS.name(), createdAccount.getCurrency().name());
        assertEquals(AccountTypeEnum.CAJA_AHORRO, createdAccount.getAccountType());
        assertNotNull(createdAccount.getUserId());
    }

    @Test
    void testCreateAccountWithInvalidCurrency() {

        AccountRequestDto accountRequestDto = new AccountRequestDto("INVALID_CURRENCY", "CAJA_AHORRO");
        HttpServletRequest request = mock(HttpServletRequest.class);

        assertThrows(IllegalArgumentException.class, () -> accountService.add(accountRequestDto, request));
    }

    @Test
    void testCreateDuplicateAccount() {

        AccountRequestDto accountRequestDto = new AccountRequestDto("ARS", "CAJA_AHORRO");
        HttpServletRequest request = mock(HttpServletRequest.class);
        Long userId = 1L;
        User mockUser = new User();
        mockUser.setId(userId);

        Accounts mockAccount1 = new Accounts
                (1L,CurrencyEnum.ARS,AccountTypeEnum.CAJA_AHORRO,100000D,
                        0D, LocalDateTime.now(),LocalDateTime.now(),false,"1111111111111111111111","alias1",mockUser);
        Accounts mockAccount2 = new Accounts(2L,CurrencyEnum.ARS,AccountTypeEnum.CAJA_AHORRO,100000D,
                0D, LocalDateTime.now(),LocalDateTime.now(),false,"1111111111111111111111","alias", mockUser);
        List<Accounts> mockAccountsList = List.of(mockAccount1,mockAccount2);


        when(userService.getIdFromRequest(request)).thenReturn(userId);
        when(userService.findById(userId)).thenReturn(Optional.of(mockUser));

        when(accountRepository.findByUserId(mockUser)).thenReturn(mockAccountsList);


        assertThrows(DuplicateAccountException.class, () -> accountService.add(accountRequestDto, request));
    }



    @Test
    void testCreateAccountWithMissingUserId() {
        // Configurar los datos necesarios para la creación de la cuenta
        AccountRequestDto accountRequestDto = new AccountRequestDto("ARS", "CAJA_AHORRO");
        HttpServletRequest request = mock(HttpServletRequest.class);

        assertThrows(UserNotFoundException.class, () -> accountService.add(accountRequestDto, request));
    }



}
