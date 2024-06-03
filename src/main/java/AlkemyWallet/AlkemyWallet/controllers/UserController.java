package AlkemyWallet.AlkemyWallet.controllers;

import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.services.JwtService;
import AlkemyWallet.AlkemyWallet.dtos.UserUpdateRequest;
import AlkemyWallet.AlkemyWallet.exceptions.UnauthorizedUserException;
import AlkemyWallet.AlkemyWallet.dtos.UserDetailDTO;
import AlkemyWallet.AlkemyWallet.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.Optional;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private JwtService jwtService;


    //Get All Users /users?page=0 ejemplo
    @GetMapping("users")
    public ResponseEntity<?> getUsers(@RequestParam(defaultValue = "0") int page){
        try {
            Page<User> users = userService.getAllUsers(page);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener todos los usuarios: " + e.getMessage());
        }
    }

//Detalle de usuario
@GetMapping("users-detail/{id}")
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
                user.getRole().getName(),
                user.getBirthDate(),
                user.getImagePath()
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

    @PostMapping("cbu/{idCbu}/users/{idUser}")
    public ResponseEntity<?> addContact(@PathVariable String idCbu, @PathVariable Long idUser, HttpServletRequest request){
        try {
            String token = jwtService.getTokenFromRequest(request);
            userService.addContact(idCbu,idUser);
            return ResponseEntity.ok(HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener todos los usuarios: " + e.getMessage());
        }
    }

    @DeleteMapping("cbu/{idCbu}/users/{idUser}")
    public ResponseEntity<?> deleteContact(@PathVariable String idCbu,@PathVariable Long idUser,HttpServletRequest request){
        try {
            Long userId = userService.getIdFromRequest(request);
            if(userId == idUser) {
                userService.deleteContact(idCbu, idUser);
                return ResponseEntity.ok(HttpStatus.OK);
            }
            else  return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener todos los usuarios: " + e.getMessage());
        }
    }

    @PatchMapping("users/{id}")
    public ResponseEntity<?> updateUserById(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest userUpdateRequest, HttpServletRequest request) {
        Long userIdFromToken = userService.getIdFromRequest(request);
        if (!userIdFromToken.equals(id)) {
            throw new UnauthorizedUserException("No tienes permiso para editar este usuario");
        }

        return ResponseEntity.ok(userService.updateUser(id,userUpdateRequest,request));

    }
}
