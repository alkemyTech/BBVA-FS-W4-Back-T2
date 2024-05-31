package AlkemyWallet.AlkemyWallet.services;

import AlkemyWallet.AlkemyWallet.config.PaginationConfig;
import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PaginationConfig paginationConfig;

    public Page<User> getAllUsers(int page) {
        int usersPerPage = paginationConfig.getUsersPerPage(); // Mostrar de a 10 usuarios por página
        Pageable pageable = PageRequest.of(page, usersPerPage);
        return userRepository.findAll(pageable);
    }


    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByUserName(username).orElseThrow();
    }

    public Long getIdFromRequest(HttpServletRequest request){
        String token = jwtService.getTokenFromRequest(request);
        String username = jwtService.getUsernameFromToken(token);
        Long id = userRepository.findByUserName(username).get().getId();
        return id;
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    };

    public void addContact(String idCbu, Long idUser) {
            User user = userRepository.findById(idUser).orElseThrow();
            user.getCbuTerceros().add(idCbu);
            userRepository.save(user); //como el usuario ya esta en la base hace un update
    }

    public void deleteContact(String idCbu, Long idUser) {
        User user = userRepository.findById(idUser).orElseThrow();
        user.getCbuTerceros().remove(idCbu);
        userRepository.save(user);
    }
}