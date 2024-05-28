package AlkemyWallet.AlkemyWallet.services;
import AlkemyWallet.AlkemyWallet.domain.Accounts;
import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.repositories.AccountRepository;
import AlkemyWallet.AlkemyWallet.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserService userService;

    public List<Accounts> findAccountsByUserId(long userId) {
        User user = userService.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return accountRepository.findByUserId(user);
    }
}
