package org.hugo.backend.users.app.services;

import org.hugo.backend.users.app.exceptions.role.RoleNotFoundException;
import org.hugo.backend.users.app.models.entities.Role;
import org.hugo.backend.users.app.repositories.RoleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = RoleService.class)
class RoleServiceImplementTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImplement roleServiceImplement;

    @Nested
    @DisplayName("Pruebas de Creación de Rol")
    class CreateRolTest{
        @Test
        @DisplayName("Verificación de Creación de Rol: Detalles Válidos del Rol Deberia Guardar con Exito")
        void testCreateRole_withValidDetails_ShouldSaveRoleSuccessfully() {
            // Arrange
            Role role = new Role();
            role.setRole("ROLE_USER");
            role.setId(1L);

            when(roleRepository.save(any(Role.class))).thenReturn(role);

            // Act
            Role savedRole = roleServiceImplement.createOne(role);

            // Assert
            assertNotNull(savedRole.getId());
            assertEquals("ROLE_USER", savedRole.getRole(),"El role es invalido se esperaba 'ROLE_USER'");
        }
    }

    @Nested
    @DisplayName("Pruebas de Actualizacion de Rol")
    class UpdateRolTest{
        @Test
        @DisplayName("Verificacion de Actualizacion de Rol: Detalles validos del Rol Deberia de actualizar con Exito")
        void testUpdateRole_WithValidDetails_ShouldUpdateRoleSuccessfully() {
            // Arrange
            Role existingRole = new Role();
            existingRole.setId(2L);
            existingRole.setRole("ROLE_TEACH");

            Role updatedRole = new Role();
            updatedRole.setId(2L);
            updatedRole.setRole("ROLE_TEACHER");

            Long roleId = 10L;

            // Mock para simular la llamada a findById
            when(roleRepository.findById(roleId)).thenReturn(Optional.of(existingRole));

            // Mock para simular la llamada a save durante la actualización
            when(roleRepository.save(any(Role.class))).thenReturn(updatedRole);

            // Act
            Role result = roleServiceImplement.updateOne(updatedRole, roleId);

            // Assert
            assertNotNull(result);
            assertEquals("ROLE_TEACHER", result.getRole(),"El role es Invalido se esperaba 'ROLE_TEACHER'");
        }

        @Test
        @DisplayName("Verificacion de Actualizacion de Rol: ID de Rol Invalido")
        void testUpdateRole_WithInvalidIDRole_ShouldUpdateRoleFailed() {
            // Arrange
            Role existingRole = new Role();
            existingRole.setId(2L);
            existingRole.setRole("ROLE_TEACH");

            Role updatedRole = new Role();
            updatedRole.setId(2L);
            updatedRole.setRole("ROLE_TEACHER");
            Long roleIdInvalid = 1000L;

            // Mock para simular la llamada a findById
            when(roleRepository.findById(roleIdInvalid)).thenReturn(Optional.empty());

            // Act y Assert
            RoleNotFoundException exception = assertThrows(RoleNotFoundException.class, () -> roleServiceImplement.updateOne(updatedRole, roleIdInvalid));
            assertEquals("Error: el ID: 1000 no existe en la base de datos", exception.getMessage(),"El mensaje de la respuesta es invalido");
        }

    }

    @Nested
    @DisplayName("Pruebas de Eliminacion de Rol")
    class DeleteRolTest{
        @Test
        @DisplayName("Verificacion de Eliminacion de Rol: ID Valido del Rol Deberia de Eliminar con Exito")
        void testDeleteRole_withIDValid_ShouldDeleteSuccessfully(){
            //arrange
            Long roleId = 1L;
            Role role = new Role();
            role.setId(1L);
            role.setRole("ROLE_USER");

            when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
            // Mock para simular la llamada a deleteById
            doNothing().when(roleRepository).deleteById(roleId);

            roleServiceImplement.deleteOne(roleId);

            // Verificar que se llamó a deleteById con el ID correcto
            verify(roleRepository, times(1)).deleteById(roleId);

        }

        @Test
        @DisplayName("Verificacion de Eliminacion de Rol:ID No Valido")
        void testDeleteRole_withIDNoValid_ShouldDeleteFailed(){
            //arrange
            Long roleId = 1L;
            Role role = new Role();
            role.setId(1L);
            role.setRole("ROLE_USER");

            when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

            RoleNotFoundException roleNotFoundException = assertThrows(RoleNotFoundException.class,()->roleServiceImplement.deleteOne(roleId));

            // Act y Assert
            assertAll(
                    ()->assertEquals("Error: el ID: 1 no existe en la base de datos",roleNotFoundException.getMessage(),
                            "El mensaje de la respuesta es invalido")
            );

        }
    }

    @Nested
    @DisplayName("Pruebas de Obtener de Rol")
    class FindRoleTest{
        @Test
        @DisplayName("Verificacion de Obtener Rol: ID Valido Deberia de Obtener Rol con Exito")
        void testFindRole_withIDValid_ShouldFindSuccessfully(){
            Long roleIdInvalid = 1L;
            Role role = new Role();
            role.setId(1L);
            role.setRole("ROLE_ADMIN");
            when(roleRepository.findById(anyLong())).thenReturn(Optional.of(role));

            Role rol = roleServiceImplement.findById(roleIdInvalid);
            assertAll(
                    () -> assertEquals(role.getId(),rol.getId(),"El id de es invalido se esperaba '1'"),
                    () ->  assertEquals(role.getRole(),rol.getRole(),"El rol es invalido se esperaba 'ROLE_ADMIN'")
            );
        }

        @Test
        @DisplayName("Verificacion de Obtener Rol: ID No Valido")
        void testFindRole_withIDNoValid_ShouldFindFailed(){
            Long roleIdInvalid = 1000L;
            when(roleRepository.findById(anyLong())).thenReturn(Optional.empty());

            RoleNotFoundException roleNotFoundException = assertThrows(RoleNotFoundException.class,()-> roleServiceImplement.findById(roleIdInvalid));
            assertEquals("Error: el ID: 1000 no existe en la base de datos",roleNotFoundException.getMessage(),"El mensaje del rol no coincide");
        }
    }

    @Nested
    @DisplayName("Pruebas de obtener roles")
    class FindRolesTest{
        @Test
        @DisplayName("Verificacion de obtener roles: Existencia de Datos")
        void testFindRoles_returnsSuccessfulRolesGet(){
            Role roleUser = new Role();
            Role roleAdmin = new Role();
            roleUser.setId(1L);
            roleUser.setRole("ROLE_USER");
            roleAdmin.setId(2L);
            roleAdmin.setRole("ROLE_ADMIN");
            List<Role> roles = new ArrayList<>();
            roles.add(roleUser);
            roles.add(roleAdmin);

            when(roleRepository.findAll()).thenReturn(roles);

            List<Role> roleList = roleServiceImplement.findAll();
            assertAll(
                    () -> assertEquals(roles.size(),roleList.size(),"El tamaño de la lista es invalido se esperaba 2"),
                    () -> assertEquals(roles.get(0).getId(),roleList.get(0).getId(),"El id es invalido se esperaba 1"),
                    () -> assertEquals(roles.get(0).getRole(), roleList.get(0).getRole(),"El role es invalido se esperaba 'ROLE_USER'"),
                    () -> assertEquals(roles.get(1).getId(),roleList.get(1).getId(),"El id es invalido se esperaba 2"),
                    () -> assertEquals(roles.get(1).getRole(),roleList.get(1).getRole(),"El role es invalido se esperaba 'ROLE_ADMIN'")
            );
        }

        @Test
        @DisplayName("Verificacion de obtener roles: Vacio")
        void testFindRolesEmpty_returnsSuccessfulRolesGet(){
            List<Role> roles = new ArrayList<>();

            when(roleRepository.findAll()).thenReturn(roles);

            List<Role> roleList = roleServiceImplement.findAll();
            assertAll(
                    () -> assertEquals(0,roleList.size(),"El tamaño de la lista es invalido se esperaba que estuviera vacia")
            );
        }
    }
}
