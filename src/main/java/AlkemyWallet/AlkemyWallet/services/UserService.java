package AlkemyWallet.AlkemyWallet.services;

import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetails findByEmail(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow();
    }

    public List<User> getAllUsers( ) {
        return userRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}


