package api.bff;

import api.bff.controller.AuthController;
import api.bff.dto.auth.LoginRequest;
import api.bff.dto.auth.LoginResponse;
import api.bff.dto.auth.RegisterRequest;
import api.bff.dto.auth.RegisterResponse;
import api.bff.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void loginReturnsOk() throws Exception {
        LoginRequest req = new LoginRequest("user", "pass");
        LoginResponse resp = new LoginResponse("token", "Nombre", "correo", "direccion", "telefono", "ROLE");
        when(authService.login(any(LoginRequest.class))).thenReturn(resp);

        mockMvc.perform(post("/api/bff/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").value("token"))
                .andExpect(jsonPath("$.nombre").value("Nombre"));
    }

    @Test
    void registerReturnsOk() throws Exception {
        RegisterRequest req = new RegisterRequest("Nombre", "Apellido", "correo", "pass", "direccion", "telefono", 1);
        RegisterResponse.RolDto rol = new RegisterResponse.RolDto(1L, 1, "ROLE", "funcion");
        RegisterResponse resp = new RegisterResponse(1L, "Nombre", "Apellido", "correo", "direccion", "telefono", rol);
        when(authService.register(any(RegisterRequest.class))).thenReturn(resp);

        mockMvc.perform(post("/api/bff/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.correo").value("correo"));
    }
}
