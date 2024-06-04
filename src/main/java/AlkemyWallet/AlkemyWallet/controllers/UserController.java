package AlkemyWallet.AlkemyWallet.controllers;

import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.exceptions.ForbiddenException;
import AlkemyWallet.AlkemyWallet.exceptions.UnauthorizedTransactionException;
import AlkemyWallet.AlkemyWallet.mappers.UserDetailMapper;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private JwtService jwtService;
    private final UserDetailMapper userDetailMapper;


    //Get All Users /users?page=0 ejemplo
    @GetMapping("users")
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


    @DeleteMapping("/id/{id}")
    public void softDeleteUserById(@PathVariable Long id) {
        userService.softDeleteById(id);
    }
//Detalle de usuario
        @GetMapping("users-detail/{id}")
        public ResponseEntity<?> getUserDetail (@PathVariable Long id){
            try {
                UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                String authenticatedUsername = userDetails.getUsername();

                UserDetailDTO userDetailDTO = userService.getUserDetail(id, authenticatedUsername);
                return ResponseEntity.ok(userDetailDTO);
            } catch (UnauthorizedTransactionException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
            } catch (ForbiddenException e) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            } catch (UsernameNotFoundException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener el usuario: " + e.getMessage());
            }
        }

        @DeleteMapping("cbu/{idCbu}/users/{idUser}")
        public ResponseEntity<?> deleteContact (@PathVariable String idCbu, @PathVariable Long
        idUser, HttpServletRequest request){
            try {
                Long userId = userService.getIdFromRequest(request);
                if (userId == idUser) {
                    userService.deleteContact(idCbu, idUser);
                    return ResponseEntity.ok(HttpStatus.OK);
                } else return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.UNAUTHORIZED);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener todos los usuarios: " + e.getMessage());
            }
        }

        @PatchMapping("users/{id}")
        public ResponseEntity<?> updateUserById (@PathVariable Long id, @Valid @RequestBody UserUpdateRequest
        userUpdateRequest, HttpServletRequest request){
            Long userIdFromToken = userService.getIdFromRequest(request);
            if (!userIdFromToken.equals(id)) {
                throw new UnauthorizedUserException("No tienes permiso para editar este usuario");
            }

            return ResponseEntity.ok(userService.updateUser(id, userUpdateRequest, request));

        }
    }
