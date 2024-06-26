package AlkemyWallet.AlkemyWallet.controllers;

import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.domain.UserContact;
import AlkemyWallet.AlkemyWallet.dtos.ContactDTO;
import AlkemyWallet.AlkemyWallet.exceptions.ForbiddenException;
import AlkemyWallet.AlkemyWallet.exceptions.UnauthorizedTransactionException;
import AlkemyWallet.AlkemyWallet.exceptions.UserNotFoundException;
import AlkemyWallet.AlkemyWallet.mappers.UserDetailMapper;
import AlkemyWallet.AlkemyWallet.services.JwtService;
import AlkemyWallet.AlkemyWallet.dtos.UserUpdateRequest;
import AlkemyWallet.AlkemyWallet.exceptions.UnauthorizedUserException;
import AlkemyWallet.AlkemyWallet.dtos.UserDetailDTO;
import AlkemyWallet.AlkemyWallet.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;
    private JwtService jwtService;
    private final UserDetailMapper userDetailMapper;

    @Operation(
            description = "Endpoint accesible a usuarios autenticados",
            summary = "Obtiene una lista de todos los usuarios con paginación",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {
                                    @Content(schema = @Schema(implementation = User.class), mediaType = "application/json")
                            }
                    ),
                    @ApiResponse(
                            description = "Error al obtener todos los usuarios",
                            responseCode = "500",
                            content = @Content(schema = @Schema(implementation = String.class), mediaType = "text/plain")
                    )
            }
    )
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

    @Operation(
            description = "Endpoint accesible a usuarios admins",
            summary = "Elimina un usuario por su ID",
            responses = {
                    @ApiResponse(
                            description = "No Content",
                            responseCode = "204"
                    ),
                    @ApiResponse(
                            description = "Error al eliminar el usuario",
                            responseCode = "500",
                            content = @Content(schema = @Schema(implementation = String.class), mediaType = "text/plain")
                    )
            }
    )
    @DeleteMapping("/id/{id}")
    public void softDeleteUserById(@PathVariable Long id) {
        userService.softDeleteById(id);
    }

    @Operation(
            description = "Endpoint accesible a usuarios autenticados",
            summary = "Obtiene los detalles de un usuario por su ID",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {
                                    @Content(schema = @Schema(implementation = UserDetailDTO.class), mediaType = "application/json")
                            }
                    ),
                    @ApiResponse(
                            description = "Transacción no autorizada",
                            responseCode = "401",
                            content = @Content(schema = @Schema(implementation = String.class), mediaType = "text/plain")
                    ),
                    @ApiResponse(
                            description = "Prohibido",
                            responseCode = "403",
                            content = @Content(schema = @Schema(implementation = String.class), mediaType = "text/plain")
                    ),
                    @ApiResponse(
                            description = "Usuario no encontrado",
                            responseCode = "404",
                            content = @Content(schema = @Schema(implementation = String.class), mediaType = "text/plain")
                    ),
                    @ApiResponse(
                            description = "Error al obtener el usuario",
                            responseCode = "500",
                            content = @Content(schema = @Schema(implementation = String.class), mediaType = "text/plain")
                    )
            }
    )
    @GetMapping("detail/{id}")
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

    @Operation(
            description = "Endpoint accesible a usuarios autenticados",
            summary = "Añade un contacto por CBU",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = HttpStatus.class), mediaType = "application/json")
                    ),
                    @ApiResponse(
                            description = "Error al añadir el contacto",
                            responseCode = "500",
                            content = @Content(schema = @Schema(implementation = String.class), mediaType = "text/plain")
                    )
            }
    )
    @PostMapping("newContact")
    public ResponseEntity<?> addContact(@RequestBody ContactDTO newContact, HttpServletRequest request){
        try {
            Long userId = userService.getIdFromRequest(request);
            userService.addContact(newContact  ,userId);
            return ResponseEntity.ok(HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al añadir el contacto: " + e.getMessage());
        }
    }

    @Operation(
            description = "Endpoint accesible a usuarios autenticados",
            summary = "Elimina un contacto por CBU",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = HttpStatus.class), mediaType = "application/json")
                    ),
                    @ApiResponse(
                            description = "Error al eliminar el contacto",
                            responseCode = "500",
                            content = @Content(schema = @Schema(implementation = String.class), mediaType = "text/plain")
                    )
            }
    )
    @DeleteMapping("/deleteContact")
    public ResponseEntity<?> deleteContact (@RequestBody ContactDTO newContact, HttpServletRequest request){
        try {
            Long userId = userService.getIdFromRequest(request);
                userService.deleteContact(newContact, userId);
                return ResponseEntity.ok(HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar el contacto: " + e.getMessage());
        }
    }

    @Operation(
            description = "Endpoint accesible a usuarios autenticados",
            summary = "Obtiene la lista de contactos de un usuario",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {
                                    @Content(schema = @Schema(implementation = UserContact.class), mediaType = "application/json")
                            }
                    ),
                    @ApiResponse(
                            description = "Unauthorized",
                            responseCode = "401",
                            content = @Content(schema = @Schema(implementation = String.class), mediaType = "text/plain")
                    ),
                    @ApiResponse(
                            description = "Forbidden",
                            responseCode = "403",
                            content = @Content(schema = @Schema(implementation = String.class), mediaType = "text/plain")
                    ),
                    @ApiResponse(
                            description = "Error al obtener los contactos",
                            responseCode = "500",
                            content = @Content(schema = @Schema(implementation = String.class), mediaType = "text/plain")
                    )
            }
    )

    @GetMapping("contactsList")
    public ResponseEntity<?> getUserContacts(HttpServletRequest request) {
        try {
            Long userId = userService.getIdFromRequest(request);
            List<UserContact> contacts = userService.getContacts(userId);
            return ResponseEntity.ok(contacts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener los contactos: " + e.getMessage());
        }
    }

    @Operation(
            description = "Endpoint accesible a usuarios autenticados",
            summary = "Actualiza un usuario por su ID",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {
                                    @Content(schema = @Schema(implementation = User.class), mediaType = "application/json")
                            }
                    ),
                    @ApiResponse(
                            description = "Unauthorized",
                            responseCode = "401",
                            content = @Content(schema = @Schema(implementation = String.class), mediaType = "text/plain")
                    ),
                    @ApiResponse(
                            description = "Forbidden",
                            responseCode = "403",
                            content = @Content(schema = @Schema(implementation = String.class), mediaType = "text/plain")
                    ),
                    @ApiResponse(
                            description = "Error al actualizar el usuario",
                            responseCode = "500",
                            content = @Content(schema = @Schema(implementation = String.class), mediaType = "text/plain")
                    )
            }
    )
    @PatchMapping("users/{id}")
    public ResponseEntity<?> updateUserById(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest userUpdateRequest, HttpServletRequest request) {
        Long userIdFromToken = userService.getIdFromRequest(request);
        if (!userIdFromToken.equals(id)) {
            throw new UnauthorizedUserException("No tienes permiso para editar este usuario");
        }

        try {
            return ResponseEntity.ok(userService.updateUser(id, userUpdateRequest, request));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e);
        }
    }
    @GetMapping("/{id}/birthdate")
    public ResponseEntity<?> getUserBirthDate(@PathVariable Long id) {
        try {
            Optional<User> userOptional = userService.findById(id);

            if (userOptional.isPresent()) {
                LocalDate birthDate = userOptional.get().getBirthDate();
                return ResponseEntity.ok(birthDate);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener la fecha de nacimiento del usuario: " + e.getMessage());
        }
    }
}

