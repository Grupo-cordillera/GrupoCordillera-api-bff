package api.bff.service;

import api.bff.dto.auth.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private MockRestServiceServer mockServer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
        RestClient restClient = RestClient.builder(restTemplate).baseUrl("http://localhost:5001").build();
        authService = new AuthService(restClient);
    }

    @AfterEach
    void tearDown() {
        mockServer.verify();
    }

    @Test
    void login_ShouldReturnLoginResponse_WhenCalled() throws JsonProcessingException {
        var loginRequest = new LoginRequest("test@example.com", "password123");
        var loginResponse = new LoginResponse();
        loginResponse.setJwt("fake-jwt-token");

        mockServer.expect(requestTo("http://localhost:5001/authenticate"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(loginResponse)));

        var result = authService.login(loginRequest);

        assertNotNull(result);
        assertEquals("fake-jwt-token", result.getJwt());
    }

    @Test
    void register_ShouldReturnRegisterResponse_WhenCalled() throws JsonProcessingException {
        var registerRequest = new RegisterRequest();
        registerRequest.setNombre("New");
        registerRequest.setApellido("User");
        registerRequest.setCorreo("newuser@example.com");
        registerRequest.setContrasena("newpassword");
        registerRequest.setDireccion("123 Main St");
        registerRequest.setTelefono("123456789");
        registerRequest.setNumero_rol(1);

        var registerResponse = new RegisterResponse();
        registerResponse.setId(1L);

        mockServer.expect(requestTo("http://localhost:5001/usuarios"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(registerResponse)));

        var result = authService.register(registerRequest);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getAllUsers_ShouldReturnUserList_WhenCalled() throws JsonProcessingException {
        var rolDto = new UserResponse.RolDto();
        rolDto.setId(1L);
        rolDto.setNombre("ROLE_ADMIN");

        var user1 = new UserResponse();
        user1.setId(1L);
        user1.setNombre("Javier");
        user1.setRol(rolDto);

        var userList = List.of(user1);

        mockServer.expect(requestTo("http://localhost:5001/usuarios"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(userList)));

        var result = authService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Javier", result.get(0).getNombre());
    }

    @Test
    void getAllRoles_ShouldReturnRolList_WhenCalled() throws JsonProcessingException {
        var rol1 = new RolResponse();
        rol1.setId(1L);
        rol1.setNombre("ROLE_ADMIN");
        var rolList = List.of(rol1);

        mockServer.expect(requestTo("http://localhost:5001/api/rol"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(rolList)));

        var result = authService.getAllRoles();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ROLE_ADMIN", result.get(0).getNombre());
    }

    @Test
    void updateUser_ShouldReturnUpdateUserResponse_WhenCalled() throws JsonProcessingException {
        Long userId = 1L;
        var updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setNombre("Javier");
        updateUserRequest.setNumero_rol(2);

        var rolUser = new RolResponse();
        rolUser.setId(2L);
        rolUser.setNombre("ROLE_USER");

        var updateUserResponse = new UpdateUserResponse();
        updateUserResponse.setId(userId);
        updateUserResponse.setNombre("Javier");
        updateUserResponse.setRol(rolUser);

        mockServer.expect(requestTo("http://localhost:5001/usuarios/" + userId))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(updateUserResponse)));

        var result = authService.updateUser(userId, updateUserRequest);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("Javier", result.getNombre());
        assertEquals("ROLE_USER", result.getRol().getNombre());
    }

    @Test
    void deleteUser_ShouldCompleteSuccessfully_WhenCalled() {
        Long userId = 1L;

        mockServer.expect(requestTo("http://localhost:5001/usuarios/" + userId))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));

        assertDoesNotThrow(() -> authService.deleteUser(userId));
    }

    @Test
    void changePassword_ShouldReturnChangePasswordResponse_WhenCalled() throws JsonProcessingException {
        Long userId = 1L;
        var changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setNewPassword("newPassword123");

        var changePasswordResponse = new ChangePasswordResponse();
        changePasswordResponse.setId(userId);
        changePasswordResponse.setNombre("Test User");

        mockServer.expect(requestTo("http://localhost:5001/usuarios/" + userId + "/change-password"))
                .andExpect(method(HttpMethod.PATCH))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(changePasswordResponse)));

        var result = authService.changePassword(userId, changePasswordRequest);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("Test User", result.getNombre());
    }
}
