package AlkemyWallet.AlkemyWallet.controllers;

import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.dtos.UserDetailDTO;
import AlkemyWallet.AlkemyWallet.services.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    //Get All Users /users?page=0 ejemplo
    @GetMapping("/users")
    public ResponseEntity<?> getUsers(@RequestParam(defaultValue = "0") int page){
        try {
            Page<User> users = userService.getAllUsers(page);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener todos los usuarios: " + e.getMessage());
        }
    }

//Detalle de usuario
@GetMapping("/users-detail/{id}")
public ResponseEntity<?> getUserDetail(@PathVariable Long id){
    try {
        // Obtener el nombre de usuario del usuario autenticado
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String authenticatedUsername = userDetails.getUsername();

        // Buscar al usuario autenticado
        Optional<User> authenticatedUser = userService.findAuthenticatedUser(authenticatedUsername);
        if (!authenticatedUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
        }

        // Verificar si el usuario autenticado coincide con el usuario solicitado
        User user = authenticatedUser.get();
        if (!user.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tiene permiso para acceder a este usuario");
        }
        // Mapear User a UserDetailDTO
        UserDetailDTO userDetailDTO = new UserDetailDTO(
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getRole().getName()
        );

        return ResponseEntity.ok(userDetailDTO);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener el usuario: " + e.getMessage());
    }
}

    @RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUserById(@PathVariable Long id) {
        try {
            userService.deleteById(id);
            return ResponseEntity.ok("Usuario eliminado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar el usuario: " + e.getMessage());
        }
    }
}
