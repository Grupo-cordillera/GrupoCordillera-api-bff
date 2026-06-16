package api.bff.controller;

import api.bff.dto.auth.*;
import api.bff.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void login_ShouldReturnOkAndLoginResponse_WhenCalled() throws Exception {
        var loginRequest = new LoginRequest("test@example.com", "password123");
        var loginResponse = new LoginResponse();
        loginResponse.setJwt("fake-jwt-token");
        loginResponse.setId(6L);
        loginResponse.setNombre("Juan");
        loginResponse.setApellido("Perez");
        loginResponse.setCorreo("juan@test.com");
        loginResponse.setDireccion("Calle 123");
        loginResponse.setTelefono("123456");
        
        var rolDto = new RolDto(1L, 1, "ADMIN", "Administrador del sistema");
        loginResponse.setRol(rolDto);

        when(authService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        mockMvc.perform(post("/api/bff/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").value("fake-jwt-token"))
                .andExpect(jsonPath("$.id").value(6L))
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.apellido").value("Perez"))
                .andExpect(jsonPath("$.correo").value("juan@test.com"))
                .andExpect(jsonPath("$.direccion").value("Calle 123"))
                .andExpect(jsonPath("$.telefono").value("123456"))
                .andExpect(jsonPath("$.rol.id").value(1L))
                .andExpect(jsonPath("$.rol.numeroRol").value(1))
                .andExpect(jsonPath("$.rol.nombre").value("ADMIN"))
                .andExpect(jsonPath("$.rol.funcion").value("Administrador del sistema"));
    }

    @Test
    void register_ShouldReturnOkAndRegisterResponse_WhenCalled() throws Exception {
        var registerRequest = new RegisterRequest();
        registerRequest.setCorreo("newuser@example.com");

        var registerResponse = new RegisterResponse();
        registerResponse.setId(1L);

        when(authService.register(any(RegisterRequest.class))).thenReturn(registerResponse);

        mockMvc.perform(post("/api/bff/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getAllUsers_ShouldReturnOkAndUserList_WhenCalled() throws Exception {
        var userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setNombre("Javier");
        var userList = List.of(userResponse);

        when(authService.getAllUsers()).thenReturn(userList);

        mockMvc.perform(get("/api/bff/auth/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nombre").value("Javier"));
    }

    @Test
    void getAllRoles_ShouldReturnOkAndRolList_WhenCalled() throws Exception {
        var rolResponse = new RolResponse();
        rolResponse.setId(1L);
        rolResponse.setNombre("ROLE_ADMIN");
        var rolList = List.of(rolResponse);

        when(authService.getAllRoles()).thenReturn(rolList);

        mockMvc.perform(get("/api/bff/auth/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nombre").value("ROLE_ADMIN"));
    }

    @Test
    void updateUser_ShouldReturnOkAndUpdatedUser_WhenCalled() throws Exception {
        Long userId = 1L;
        var updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setNombre("Javier");

        var updateUserResponse = new UpdateUserResponse();
        updateUserResponse.setId(userId);
        updateUserResponse.setNombre("Javier");

        when(authService.updateUser(eq(userId), any(UpdateUserRequest.class))).thenReturn(updateUserResponse);

        mockMvc.perform(put("/api/bff/auth/usuarios/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.nombre").value("Javier"));
    }

    @Test
    void deleteUser_ShouldReturnNoContent_WhenCalled() throws Exception {
        Long userId = 1L;
        doNothing().when(authService).deleteUser(userId);

        mockMvc.perform(delete("/api/bff/auth/usuarios/{id}", userId))
                .andExpect(status().isNoContent());
    }

    @Test
    void changePassword_ShouldReturnOkAndSuccessMessage_WhenCalled() throws Exception {
        Long userId = 1L;
        var changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setNewPassword("newPassword123");

        var changePasswordResponse = new ChangePasswordResponse();
        changePasswordResponse.setId(userId);

        when(authService.changePassword(eq(userId), any(ChangePasswordRequest.class))).thenReturn(changePasswordResponse);

        mockMvc.perform(patch("/api/bff/auth/usuarios/{id}/change-password", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId));
    }
}
