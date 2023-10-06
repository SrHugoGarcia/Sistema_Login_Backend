package org.hugo.backend.users.app.controllers;

import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hugo.backend.users.app.controllers.dto.ApiResponse;
import org.hugo.backend.users.app.exceptions.role.RoleNotFoundException;
import org.hugo.backend.users.app.models.entities.Role;
import org.hugo.backend.users.app.services.RoleService;
import org.hugo.backend.users.app.utils.ResponseError;
import org.hugo.backend.users.app.utils.StatusType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(controllers = RoleController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
//@AutoConfigureMockMvc(addFilters = false)//Desactiva filtros de seguridad
@MockBean({RoleService.class})
class RoleControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    RoleService roleService;
    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("Pruebas de Creación de Rol")
    class CreateRoleTests {
        @Test
        @DisplayName("Verificación de Creación de Rol: Detalles Válidos del Rol Resultan en una Creación Exitosa")
        void testCreateRole_withValidDetails_returnsSuccessfulRoleCreation() throws Exception {
            // Dados
            Role role = new Role();
            role.setRole("ROLE_CAPA");
            role.setId(1L);

            // Simulación del servicio de creación de roles
            when(roleService.createOne(any(Role.class))).thenReturn(role);

            // Cuando
            MvcResult mvcResult = mockMvc.perform(post("/api/v1/roles")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(role)))
                    .andReturn();

            ApiResponse apiResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ApiResponse.class);

            // Entonces
            assertAll(
                    () -> assertEquals(StatusType.SUCCESSFUL, apiResponse.getStatus(), "El estado de la respuesta debería indicar una creación exitosa"),
                    () -> assertEquals("Role creado con exito", apiResponse.getMessage(), "El mensaje de la respuesta debería ser 'Role creado con exito'"),
                    () -> {
                        Role roleCreate = objectMapper.readValue(objectMapper.writeValueAsString(apiResponse.getData()), Role.class);
                        assertEquals(role.getRole(), roleCreate.getRole(), "El rol creado debería coincidir con el rol proporcionado");
                        assertEquals(role.getId(), roleCreate.getId(), "El ID creado debería coincidir con el ID proporcionado");
                    }
            );
            System.out.println(mvcResult.getResponse().getContentAsString());
        }

        @Test
        @DisplayName("Verificación de Creación de Rol: Nombre del Rol Vacío")
        void testCreateRole_withEmptyRoleName_returnsFailedRoleCreation() throws Exception {
            //Dados
            Role role = new Role();
            role.setRole("");
            role.setId(1L);

            when(roleService.createOne(any(Role.class))).thenReturn(role);

            // Cuando
            MvcResult mvcResult = mockMvc.perform(post("/api/v1/roles")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(role)))
                    .andReturn();

            ResponseError responseError = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ResponseError.class);
            // Entonces
            assertAll(
                    () -> assertEquals(400, mvcResult.getResponse().getStatus(), "Invalido status code retornado"),
                    () -> assertEquals(StatusType.FAIL, responseError.getStatus(), "El estado de la respuesta debería ser fallida"),
                    () -> assertEquals("El campo: role no puede estar vacio", responseError.getError(), "El error de la respuesta debería ser 'El campo: role no puede estar vacio'"),
                    () -> assertEquals("Error en la creacion del role", responseError.getMessage(), "El mensaje de la respuesta debería ser 'Error en la creacion del role'")
            );
            System.out.println(mvcResult.getResponse().getContentAsString());
        }

        @Test
        @DisplayName("Verificación de Creación de Rol: Nombre del Rol Inválido (No puede exceder de 50 caracteres)")
        void testCreateRole_withRoleNameExceeding50Characters() throws Exception {
            Role role = new Role();
            role.setId(1L);
            role.setRole("UnNombreDeRolConMasDe50CaracteresParaProbarLaRestriccion");

            when(roleService.createOne(any(Role.class))).thenReturn(role);

            MvcResult mvcResult = mockMvc.perform(post("/api/v1/roles")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(role))).andReturn();

            ResponseError responseError = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ResponseError.class);
            assertAll(() -> assertEquals(400, mvcResult.getResponse().getStatus(), "Invalido status code retornado"),
                    () -> assertEquals(StatusType.FAIL, responseError.getStatus(), "El estado de la respuesta debería ser fallida"),
                    () -> assertEquals("El campo: role solo puede tener como maximo 50 caracteres", responseError.getError(), "El error de la respuesta debería ser 'El campo: role no puede estar vacio'"),
                    () -> assertEquals("Error en la creacion del role", responseError.getMessage(), "El mensaje de la respuesta debería ser 'Error en la creacion del role'"));
            System.out.println(mvcResult.getResponse().getContentAsString());
        }
    }

    @Nested
    @DisplayName("Pruebas de actualización de rol")
    class UpdateRoleTest {
        @Test
        @DisplayName("Verificación de Actualizacion de Rol: Detalles Válidos de Rol Resultan en una Actualizacion Exitosa")
        void testUpdateRole_withValidDetails_returnsSuccessfulRoleUpdate() throws Exception {
            Role role = new Role();
            role.setId(1L);
            role.setRole("ROLE_ADMINISTRADOR");
            Long roleId = 1L;
            when(roleService.updateOne(any(Role.class), anyLong())).thenReturn(role);

            MvcResult mvcResult = mockMvc.perform(put("/api/v1/roles/{id}", roleId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(role))).andReturn();

            ApiResponse apiResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ApiResponse.class);

            assertAll(() -> assertEquals(200, mvcResult.getResponse().getStatus(),
                            "El estado de la respuesta debería indicar una actualizacion exitosa"),
                    () -> assertEquals("Role actualizado con exito", apiResponse.getMessage(), "El mensaje de la respuesta debería ser 'Role actualizado con exito'"),
                    () -> {
                        Role roleUpdate = objectMapper.readValue(objectMapper.writeValueAsString(apiResponse.getData()), Role.class);
                        assertEquals(role.getRole(), roleUpdate.getRole(), "El rol actualizado debería coincidir con el rol proporcionado");
                        assertTrue(roleUpdate.getId() != null ? true : false, "Deberia de regresar el id del rol actualizado");
                    });
            System.out.println(mvcResult.getResponse().getContentAsString());
        }

        @Test
        @DisplayName("Verificación de Actualización de Rol: Detalles Inválidos de Rol Resultan en una Actualización fallida")
        void testUpdateRole_withEmptyRole_returnsFailedRoleUpdate() throws Exception {
            // Dados
            Role role = new Role();
            role.setRole("");
            Long roleId = 1L;

            // Simulación del servicio de actualización de roles
            when(roleService.updateOne(any(Role.class), anyLong())).thenReturn(role);

            // Cuando
            MvcResult mvcResult = mockMvc.perform(put("/api/v1/roles/{id}", roleId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(role)))
                    .andReturn();

            // Entonces
            ResponseError responseError = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ResponseError.class);

            assertAll(
                    () -> assertEquals(400, mvcResult.getResponse().getStatus(), "El estado de la respuesta debería indicar una actualización fallida"),
                    () -> assertEquals(StatusType.FAIL, responseError.getStatus(), "El estado de la respuesta debería ser fallido"),
                    () -> assertEquals("El campo: role no puede estar vacio", responseError.getError(), "El error de la respuesta debería ser 'El campo: role no puede estar vacio'"),
                    () -> assertEquals("Error en la actualizaciÃ³n del role", responseError.getMessage(), "El mensaje de la respuesta debería ser 'Error en la actualizaciÃ³n del role'")
            );

            System.out.println(mvcResult.getResponse().getContentAsString());
        }

        @Test
        @DisplayName("Verificación de Actualización de Rol: Intento de Actualizar un Rol Inexistente")
        void testUpdateNonexistentRole_returnsNotFound() throws Exception {
            // Dados
            Role role = new Role();
            role.setRole("ROLE_ADMINISTRADOR");
            Long roleId = 1L;

            // Simulación del servicio de actualización de roles para un rol inexistente
            when(roleService.updateOne(any(Role.class), anyLong())).thenThrow(new RoleNotFoundException("Error: el ID: 1  no existe en la base de datos"));

            // Cuando
            MvcResult mvcResult = mockMvc.perform(put("/api/v1/roles/{id}", roleId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(role)))
                    .andReturn();

            ResponseError responseError = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ResponseError.class);
            // Entonces
            assertAll(
                    () -> assertEquals(404, mvcResult.getResponse().getStatus(), "El estado de la respuesta debería indicar una actualización fallida"),
                    () -> assertEquals(StatusType.FAIL, responseError.getStatus(), "El estado de la respuesta debería ser fallido"),
                    () -> assertEquals("Error: el ID: 1  no existe en la base de datos", responseError.getError(), "El error de la respuesta debería ser 'Error: el ID: 1  no existe en la base de datos'"),
                    () -> assertEquals("Error al buscar el role", responseError.getMessage(), "El mensaje de la respuesta debería ser 'Error al buscar el role'")
            );
            System.out.println(mvcResult.getResponse().getContentAsString());
        }


        @Test
        @DisplayName("Verificación de Actualización de Rol: ID no valido(No es un numero)")
        void testUpdateRole_withInvalidNameInUrl_returnsBadRequest() throws Exception {
            // Dados
            Role role = new Role();
            role.setRole("ROLE_ADMINISTRADOR");
            String roleIdInvalid = "invalidId";

            when(roleService.updateOne(any(Role.class), anyLong())).thenReturn(null);

            // Cuando
            MvcResult mvcResult = mockMvc.perform(put("/api/v1/roles/{id}", roleIdInvalid)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(role)))
                    .andReturn();

            // Entonces
            ResponseError responseError = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ResponseError.class);

            assertAll(
                    () -> assertEquals(400, mvcResult.getResponse().getStatus(), "El estado de la respuesta debería indicar una actualización fallida"),
                    () -> assertEquals(StatusType.FAIL, responseError.getStatus(), "El estado de la respuesta debería ser fallido"),
                    () -> assertEquals("Error al convertir el valor: invalidId a tipo Long", responseError.getError(), "El error de la respuesta debería ser 'Error al convertir el valor: invalidId a tipo Long'"),
                    () -> assertEquals("No se puede convertir el valor", responseError.getMessage(), "El mensaje de la respuesta debería ser 'No se puede convertir el valor'")
            );
            System.out.println(mvcResult.getResponse().getContentAsString());
        }

    }


    @Nested
    @DisplayName("Pruebas de obtener rol por id")
    class FindByIdRoleTest {
        @Test
        @DisplayName("Verificación de Obtencion de  Rol: Detalles Válidos de Rol Resultan en una obtencion Exitosa")
        void testGetRoleById_withValidDetails_returnsSuccessfulRoleGet() throws Exception {
            Role role = new Role();
            role.setRole("ROLE_USER");
            role.setId(1L);
            Long roleId = 1L;
            when(roleService.findById(anyLong())).thenReturn(role);

            MvcResult mvcResult = mockMvc.perform(get("/api/v1/roles/{id}", roleId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(role))).andReturn();

            ApiResponse apiResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ApiResponse.class);

            assertAll(() -> assertEquals(200, mvcResult.getResponse().getStatus(),
                            "El estado de la respuesta debería indicar una obtencion exitosa"),
                    () -> assertEquals("Role obtenido con exito", apiResponse.getMessage(), "El mensaje de la respuesta debería ser 'Role obtenido con exito'"),
                    () -> {
                        Role roleGet = objectMapper.readValue(objectMapper.writeValueAsString(apiResponse.getData()), Role.class);
                        assertEquals(role.getRole(), roleGet.getRole(), "El rol obtenido debería coincidir con el rol proporcionado");
                        assertEquals(role.getId(), roleGet.getId(), "El id rol obtenido debería coincidir con el rol proporcionado");
                    });
            System.out.println(mvcResult.getResponse().getContentAsString());
        }

        @Test
        @DisplayName("Verificación de Obtencion de Rol: ID no valido(No es un numero)")
        void testGetRoleById_withInvalidIDRole_returnBadRequest() throws Exception {
            Role role = new Role();
            role.setRole("ROLE_USER");
            role.setId(1L);
            String roleIdInvalid = "invalidId";

            when(roleService.findById(anyLong())).thenReturn(role);

            MvcResult mvcResult = mockMvc.perform(get("/api/v1/roles/{id}", roleIdInvalid)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(role))).andReturn();

            ResponseError responseError = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ResponseError.class);

            assertAll(
                    () -> assertEquals(400, mvcResult.getResponse().getStatus(), "El estado de la respuesta debería indicar una actualización fallida"),
                    () -> assertEquals(StatusType.FAIL, responseError.getStatus(), "El estado de la respuesta debería ser fallido"),
                    () -> assertEquals("Error al convertir el valor: invalidId a tipo Long", responseError.getError(), "El error de la respuesta debería ser 'Error al convertir el valor: invalidId a tipo Long'"),
                    () -> assertEquals("No se puede convertir el valor", responseError.getMessage(), "El mensaje de la respuesta debería ser 'No se puede convertir el valor'")
            );
            System.out.println(mvcResult.getResponse().getContentAsString());
        }

        @Test
        @DisplayName("Verificación de Obtencion de Rol: ID No Valido(100)")
        void testGetRoleById_NonexistentRole_returnsNotFound() throws Exception {
            // Dados
            Role role = new Role();
            role.setRole("ROLE_ADMINISTRADOR");
            long roleId = 100L;

            // Simulación del servicio de actualización de roles para un rol inexistente
            when(roleService.updateOne(any(Role.class), anyLong())).thenThrow(new RoleNotFoundException("Error: el ID: 100  no existe en la base de datos"));

            // Cuando
            MvcResult mvcResult = mockMvc.perform(put("/api/v1/roles/{id}", roleId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(role)))
                    .andReturn();

            ResponseError responseError = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ResponseError.class);
            // Entonces
            assertAll(
                    () -> assertEquals(404, mvcResult.getResponse().getStatus(), "El estado de la respuesta debería indicar una actualización fallida"),
                    () -> assertEquals(StatusType.FAIL, responseError.getStatus(), "El estado de la respuesta debería ser fallido"),
                    () -> assertEquals("Error: el ID: 100  no existe en la base de datos", responseError.getError(), "El error de la respuesta debería ser 'Error: el ID: 100  no existe en la base de datos'"),
                    () -> assertEquals("Error al buscar el role", responseError.getMessage(), "El mensaje de la respuesta debería ser 'Error al buscar el role'")
            );
            System.out.println(mvcResult.getResponse().getContentAsString());
        }


    }

    @Nested
    @DisplayName("Pruebas de eliminar rol por id")
    class DeleteRoleTest {
        @Test
        @DisplayName("Verificacion de Eliminacion de Rol: ID Valido de Rol Resulta en una Eliminacion Exitosa")
        void testDeleteRole_withIdValid_returnsNoContent() throws Exception {
            // Dados
            long roleId = 1L;

            // Simulación del servicio de eliminación de roles por ID
            doNothing().when(roleService).deleteOne(anyLong());
            //Cuando
            MvcResult mvcResult = mockMvc.perform(delete("/api/v1/roles/{id}", roleId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn();
            //Entonces
            assertEquals(204, mvcResult.getResponse().getStatus(), "El estado de la respuesta debería indicar una eliminación exitosa");
        }

        @Test
        @DisplayName("Verificacion de Eliminacion de Rol: ID No Valido(2000)")
        void testDeleteRole_withInvalidRole_returnsNotFound() throws Exception {
            //Dados
            Long roleId = 2000L;
            doThrow(new RoleNotFoundException("Error: el ID: 2000 no existe en la base de datos")).when(roleService).deleteOne(anyLong());

            //Cuando
            MvcResult mvcResult = mockMvc.perform(delete("/api/v1/roles/1", roleId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn();

            ResponseError responseError = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ResponseError.class);
            assertAll(
                    () -> assertEquals(404, mvcResult.getResponse().getStatus(), "El estado de la respuesta debería indicar una eliminacion fallida"),
                    () -> assertEquals(StatusType.FAIL, responseError.getStatus(), "El estado de la respuesta deberia de ser fallida"),
                    () -> assertEquals("Error al buscar el role", responseError.getMessage(), "El mensaje de la respuesta deberia ser 'Error al buscar el role'"),
                    () -> assertEquals("Error: el ID: 2000 no existe en la base de datos", responseError.getError(), "El error de la respuesta deberia de ser 'Error: el ID: 2000 no existe en la base de datos'")
            );
            System.out.println(mvcResult.getResponse().getContentAsString());
        }

        @Test
        @DisplayName("Verificacion de Eliminacion de Rol:ID No Valido(No es un numero)")
        void testDeleteRole_withInvalidIDRole_returnBadRequest() throws Exception {
            //Dados
            String roleIdInvalid = "invalidId";
            doNothing().when(roleService).deleteOne(anyLong());

            //Cuando
            MvcResult mvcResult = mockMvc.perform(delete("/api/v1/roles/{id}", roleIdInvalid)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)).andReturn();

            ResponseError responseError = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ResponseError.class);
            assertAll(
                    () -> assertEquals(400, mvcResult.getResponse().getStatus(), "El estado de la respuesta debería indicar una eliminacion fallida"),
                    () -> assertEquals(StatusType.FAIL, responseError.getStatus(), "El estado de la respuesta deberia de ser fallida"),
                    () -> assertEquals("Error al convertir el valor: invalidId a tipo Long", responseError.getError(), "El error de la respuesta debería ser 'Error al convertir el valor: invalidId a tipo Long'"),
                    () -> assertEquals("No se puede convertir el valor", responseError.getMessage(), "El mensaje de la respuesta debería ser 'No se puede convertir el valor'")
            );
            System.out.println(mvcResult.getResponse().getContentAsString());
        }
    }

    @Nested
    @DisplayName("Pruebas de obtener roles")
    class GetRolesTest {
        @Test
        @DisplayName("Verificación de Obtención de Roles: Existencia de Datos")
        void testGetRoles_returnsSuccessfulRolesGet() throws Exception {
            // Dados
            Role roleUser = new Role();
            roleUser.setId(1L);
            roleUser.setRole("ROLE_USER");

            Role roleAdmin = new Role();
            roleAdmin.setId(2L);
            roleAdmin.setRole("ROLE_ADMIN");

            List<Role> roles = Arrays.asList(roleUser, roleAdmin);

            when(roleService.findAll()).thenReturn(roles);

            // Cuando
            MvcResult mvcResult = mockMvc.perform(get("/api/v1/roles")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andReturn();

            // Entonces
            ApiResponse apiResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ApiResponse.class);
            Map<String, List<Role>> rolesMap = (Map<String, List<Role>>) apiResponse.getData();
            List<Role> rolesList = rolesMap.get("roles");
            Map<String, Object> role1 = (Map<String, Object>) rolesList.get(0);
            Map<String, Object> role2 = (Map<String, Object>) rolesList.get(1);
            assertAll(
                    () -> assertEquals(200, mvcResult.getResponse().getStatus(), "El estado de la respuesta debería indicar una obtención exitosa"),
                    () -> assertEquals(StatusType.SUCCESSFUL, apiResponse.getStatus(), "El estado de la respuesta debería ser exitoso"),
                    () -> assertEquals("Roles obtenidos con exito", apiResponse.getMessage(), "El mensaje de la respuesta debería ser 'Roles obtenidos con éxito'"),
                    () -> assertNotNull(apiResponse.getData(), "La respuesta debería contener datos"),
                    () -> assertEquals(roleUser.getId().intValue(), role1.get("id"), "El ID del primer rol debería ser 1"), 
                    () -> assertEquals("ROLE_USER", role1.get("role"), "El rol del primer rol debería ser ROLE_USER"), 
                    () -> assertEquals(roleAdmin.getId().intValue(), role2.get("id"), "El ID del segundo rol debería ser 2"), 
                    () -> assertEquals("ROLE_ADMIN", role2.get("role"), "El rol del segundo rol debería ser ROLE_ADMIN")
            );
        }

        @Test
        @DisplayName("Verificacion de Obtencion de roles: Vacio")
        void testGetRolesEmpty_returnSuccessfulRolesGet() throws Exception {
            List<Role> roleList = new ArrayList<>();
            when(roleService.findAll()).thenReturn(roleList);

            MvcResult mvcResult = mockMvc.perform(get("/api/v1/roles")
                    .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andReturn();

            ApiResponse apiResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),ApiResponse.class);
            Map<String,Role> roleMap = (Map<String, Role>) apiResponse.getData();
            List<Role> rolesList = (List<Role>) roleMap.get("roles");

            System.out.printf(mvcResult.getResponse().getContentAsString());
            assertAll(
                    () -> assertEquals(200, mvcResult.getResponse().getStatus(), "El estado de la respuesta debería indicar una obtención exitosa"),
                    () -> assertEquals(StatusType.SUCCESSFUL, apiResponse.getStatus(), "El estado de la respuesta debería ser exitoso"),
                    () -> assertEquals("Roles obtenidos con exito",apiResponse.getMessage(),"El mensaje de la respuesta deberia de ser 'Roles obtenidos con exito'"),
                    () -> assertEquals(roleList.size(),rolesList.size())
            );
        }

    }
}