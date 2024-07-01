package AlkemyWallet.AlkemyWallet.services;

import AlkemyWallet.AlkemyWallet.config.PaginationConfig;
import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.domain.UserContact;
import AlkemyWallet.AlkemyWallet.dtos.ContactDTO;
import AlkemyWallet.AlkemyWallet.dtos.UserDetailDTO;
import AlkemyWallet.AlkemyWallet.dtos.UserUpdateRequest;
import AlkemyWallet.AlkemyWallet.exceptions.ForbiddenException;
import AlkemyWallet.AlkemyWallet.exceptions.UnauthorizedUserException;
import AlkemyWallet.AlkemyWallet.exceptions.UserNotFoundException;
import AlkemyWallet.AlkemyWallet.mappers.UserDetailMapper;
import AlkemyWallet.AlkemyWallet.repositories.UserRepository;
import AlkemyWallet.AlkemyWallet.repositories.userContactRepository;
import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PaginationConfig paginationConfig;
    private final UserDetailMapper userDetailMapper;
    private final PasswordEncoder passwordEncoder;
    private final userContactRepository userContactRepository;

    public Page<User> getAllUsers(int page) {
        int usersPerPage = paginationConfig.getUsersPerPage(); // Mostrar de a 10 usuarios por página
        Pageable pageable = PageRequest.of(page, usersPerPage);
        return userRepository.findAll(pageable);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUserName(username);
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
    public List<User> getAllUsers( ) {
        return userRepository.findAll();
    }


    public void softDeleteById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setSoftDelete(!user.isSoftDelete());
        userRepository.save(user);
    }


    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    };

    public Object updateUser(Long id, UserUpdateRequest userUpdateRequest, HttpServletRequest request)  {

        // Obtener el usuario a editar
        User existingUser = findById(id).orElseThrow(() -> new UserNotFoundException("No se encontro un usuario con ese Id") );

        // Actualizar los campos permitidos
        if (userUpdateRequest.getFirstName() != null) {
            existingUser.setFirstName(userUpdateRequest.getFirstName());
        }
        if (userUpdateRequest.getLastName() != null) {
            existingUser.setLastName(userUpdateRequest.getLastName());
        }
        if (userUpdateRequest.getPassword() != null) {
            existingUser.setPassword(passwordEncoder.encode(userUpdateRequest.getPassword()));
        }

        existingUser.setUpdateDate(LocalDateTime.now());

        userRepository.save(existingUser);

        return existingUser;
    }

    public void addContact(ContactDTO newContactDTO, Long idUser) {
            User user = userRepository.findById(idUser).orElseThrow();
            UserContact newContact = new UserContact(newContactDTO.getName(), newContactDTO.getCbu(),user);
            userContactRepository.save(newContact);
            user.getContacts().add(newContact);
            userRepository.save(user);
    }

    public void deleteContact(ContactDTO newContactDTO, Long idUser) {
        // Obtener el usuario
        User user = userRepository.findById(idUser)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Buscar el contacto por CBU
        UserContact contact = userContactRepository.findByCbu(newContactDTO.getCbu())
                .orElseThrow(() -> new RuntimeException("Contacto no encontrado"));

        // Remover el contacto del usuario
        user.getContacts().remove(contact);

        // Guardar los cambios en el usuario
        userRepository.save(user);

        // Eliminar el contacto de la base de datos
        userContactRepository.delete(contact);
    }

    public List<UserContact> getContacts(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        List<UserContact> contacts = userContactRepository.findByUser(user);
        return contacts;
    }

    public Optional<User> findAuthenticatedUser(String username){
        return userRepository.findByUserName(username);
    }

    public UserDetailDTO getUserDetail(Long id, String authenticatedUsername) {
        Optional<User> authenticatedUser = userRepository.findByUserName(authenticatedUsername);

        if (!authenticatedUser.isPresent()) {
            throw new UnauthorizedUserException("Usuario no autenticado");
        }

        User user = authenticatedUser.get();

        if (!user.getId().equals(id)) {
            throw new ForbiddenException("No tiene permiso para acceder a este usuario");
        }

        return userDetailMapper.toUserDetailDto(user);
    }

}
