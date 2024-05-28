package AlkemyWallet.AlkemyWallet.services;

import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetails findByUserName(String email) throws UsernameNotFoundException {
        return userRepository.findByUserName(email).orElseThrow();
    }

    public List<User> getAllUsers( ) {
        return userRepository.findAll();
    }


    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByUserName(username).orElseThrow();
    }


    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    };
}

