package AlkemyWallet.AlkemyWallet.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import AlkemyWallet.AlkemyWallet.domain.Role;
import AlkemyWallet.AlkemyWallet.domain.factory.RoleFactory;
import AlkemyWallet.AlkemyWallet.enums.RoleEnum;
import AlkemyWallet.AlkemyWallet.repositories.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;


public class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleFactory roleFactory;

    @Captor
    private ArgumentCaptor<Role> roleCaptor;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateRoleWithMandatoryFieldsAndTimestamps() {
        // Configuración de la prueba
        when(roleRepository.findByName(RoleEnum.ADMIN)).thenReturn(null);
        when(roleRepository.findByName(RoleEnum.USER)).thenReturn(null);
        when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> {
            Role roleToSave = invocation.getArgument(0);
            roleToSave.setCreationDate(LocalDateTime.now());
            roleToSave.setUpdateDate(LocalDateTime.now());
            return roleToSave;
        });

        // Llamada al método que crea los roles
        roleFactory.initializeRoles();

        // Verificación
        assertNotNull(RoleFactory.getAdminRole());
        assertEquals(RoleEnum.ADMIN, RoleFactory.getAdminRole().getName());
        assertEquals("Administrator role", RoleFactory.getAdminRole().getDescription());
        assertNotNull(RoleFactory.getAdminRole().getCreationDate());
        assertNotNull(RoleFactory.getAdminRole().getUpdateDate());

        assertNotNull(RoleFactory.getUserRole());
        assertEquals(RoleEnum.USER, RoleFactory.getUserRole().getName());
        assertEquals("User role", RoleFactory.getUserRole().getDescription());
        assertNotNull(RoleFactory.getUserRole().getCreationDate());
        assertNotNull(RoleFactory.getUserRole().getUpdateDate());

        // Verificar que el rol de admin se guarde correctamente en el repositorio
        verify(roleRepository, times(2)).save(any(Role.class));
    }

}

