package AlkemyWallet.AlkemyWallet.test;

import AlkemyWallet.AlkemyWallet.controllers.UserController;
import AlkemyWallet.AlkemyWallet.domain.User;
import AlkemyWallet.AlkemyWallet.dtos.UserUpdateRequest;
import AlkemyWallet.AlkemyWallet.exceptions.UnauthorizedUserException;
import AlkemyWallet.AlkemyWallet.exceptions.UserNotFoundException;
import AlkemyWallet.AlkemyWallet.services.JwtService;
import AlkemyWallet.AlkemyWallet.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUpdateUserByIdSuccess() throws Exception {
        // Arrange
        Long userId = 1L;
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setFirstName("Juan");
        userUpdateRequest.setLastName("Pecados");
        userUpdateRequest.setPassword("password123");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setFirstName("Juan");
        updatedUser.setLastName("Pecados");
        updatedUser.setUserName("newemail@example.com");

        when(userService.getIdFromRequest(any(HttpServletRequest.class))).thenReturn(userId);
        when(userService.updateUser(anyLong(), any(UserUpdateRequest.class), any(HttpServletRequest.class)))
                .thenReturn(updatedUser);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid_token");

        // Act
        ResponseEntity<?> response = userController.updateUserById(userId, userUpdateRequest, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedUser, response.getBody());
    }

    @Test
    public void testUpdateUserByIdUnauthorized() throws Exception {
        // Arrange
        Long userId = 1L;
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setFirstName("Juan");
        userUpdateRequest.setLastName("Pecados");
        userUpdateRequest.setPassword("password123");

        when(userService.getIdFromRequest(any(HttpServletRequest.class))).thenReturn(2L);
        when(userService.updateUser(anyLong(), any(UserUpdateRequest.class), any(HttpServletRequest.class)))
                .thenThrow(new UnauthorizedUserException("No tienes permiso para editar este usuario"));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid_token");

        // Act & Assert
        assertThrows(UnauthorizedUserException.class, () -> {
            userController.updateUserById(userId, userUpdateRequest, request);
        });
    }

    @Test
    public void testUpdateUserByIdNotFound() throws Exception {
        // Arrange
        Long userId = 1L;
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setFirstName("Juan");
        userUpdateRequest.setLastName("Pecados");
        userUpdateRequest.setPassword("password123");

        when(userService.getIdFromRequest(any(HttpServletRequest.class))).thenReturn(userId);
        when(userService.updateUser(anyLong(), any(UserUpdateRequest.class), any(HttpServletRequest.class)))
                .thenThrow(new UserNotFoundException("User not found"));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid_token");

        // Act
        ResponseEntity<?> response = userController.updateUserById(userId, userUpdateRequest, request);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", ((UserNotFoundException) response.getBody()).getMessage());
    }

}
