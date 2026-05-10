package api.bff.controller;

import api.bff.dto.auth.LoginRequest;
import api.bff.dto.auth.LoginResponse;
import api.bff.dto.auth.RegisterRequest;
import api.bff.dto.auth.RegisterResponse;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    private LoginRequest loginRequest;
    private LoginResponse loginResponse;
    private RegisterRequest registerRequest;
    private RegisterResponse registerResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

        // Corregido: Usamos el constructor con argumentos y el nombre de campo 'username'.
        loginRequest = new LoginRequest("test@example.com", "password123");

        loginResponse = new LoginResponse();
        loginResponse.setJwt("fake-jwt-token");
        loginResponse.setNombre("Test User");
        loginResponse.setCorreo("test@example.com");
        loginResponse.setRol("USER");

        // Corregido: Usamos el constructor con argumentos y los nombres de campo correctos.
        registerRequest = new RegisterRequest(
                "New",
                "User",
                "newuser@example.com",
                "newpassword",
                "123 Main St",
                "123456789", // telefono
                1            // numero_rol
        );

        registerResponse = new RegisterResponse();
        registerResponse.setId(1L);
        registerResponse.setNombre("New");
        registerResponse.setApellido("User");
        registerResponse.setCorreo("newuser@example.com");
    }

    @Test
    void login_ShouldReturnOkAndLoginResponse_WhenCalled() throws Exception {
        when(authService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        mockMvc.perform(post("/api/bff/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").value("fake-jwt-token"))
                .andExpect(jsonPath("$.nombre").value("Test User"))
                .andExpect(jsonPath("$.correo").value("test@example.com"))
                .andExpect(jsonPath("$.rol").value("USER"));
    }

    @Test
    void register_ShouldReturnOkAndRegisterResponse_WhenCalled() throws Exception {
        when(authService.register(any(RegisterRequest.class))).thenReturn(registerResponse);

        mockMvc.perform(post("/api/bff/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("New"))
                .andExpect(jsonPath("$.correo").value("newuser@example.com"));
    }
}
