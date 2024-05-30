package AlkemyWallet.AlkemyWallet.controllers;

import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.services.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    //Get All Users /users?page=0 ejemplo
    @GetMapping("/users")
    public ResponseEntity<?> getUsers(@RequestParam(defaultValue = "0") int page) {
        try {
            Page<User> usersPage = userService.getAllUsers(page);
            int totalPages = usersPage.getTotalPages();

            Map<String, Object> response = new HashMap<>();
            response.put("users", usersPage.getContent());
            response.put("currentPage", page);
            response.put("totalPages", totalPages);

            if (page < totalPages - 1) {
                response.put("nextPage", "/users?page=" + (page + 1));
            }
            if (page > 0) {
                response.put("previousPage", "/users?page=" + (page - 1));
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener todos los usuarios: " + e.getMessage());
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
