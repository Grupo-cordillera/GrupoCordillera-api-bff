package api.bff.service;

import api.bff.dto.auth.LoginRequest;
import api.bff.dto.auth.LoginResponse;
import api.bff.dto.auth.RegisterRequest;
import api.bff.dto.auth.RegisterResponse;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    // El servidor de prueba que interceptará las llamadas de RestClient.
    private MockRestServiceServer mockServer;

    // Un objeto para convertir objetos Java a JSON y viceversa.
    private final ObjectMapper objectMapper = new ObjectMapper();

    // La instancia de AuthService que vamos a probar.
    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        // Para usar MockRestServiceServer, necesitamos un RestTemplate.
        RestTemplate restTemplate = new RestTemplate();
        // El servidor de prueba se asocia al RestTemplate para interceptar sus llamadas.
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
        // Creamos el RestClient a partir del RestTemplate que ya está siendo monitoreado por el mockServer.
        RestClient restClient = RestClient.builder(restTemplate).baseUrl("http://localhost:5001").build();
        // Inyectamos el RestClient de prueba en nuestro servicio.
        authService = new AuthService(restClient);
    }

    @AfterEach
    void tearDown() {
        // Verificamos que todas las expectativas que configuramos en el mockServer se hayan cumplido.
        mockServer.verify();
    }

    @Test
    void login_ShouldReturnLoginResponse_WhenCalled() throws JsonProcessingException {
        // Preparamos los datos de prueba.
        var loginRequest = new LoginRequest("test@example.com", "password123");
        var loginResponse = new LoginResponse();
        loginResponse.setJwt("fake-jwt-token");

        // Configuramos el servidor de prueba para que espere una petición POST a "/authenticate"
        // y devuelva una respuesta exitosa (200 OK) con el cuerpo de loginResponse en formato JSON.
        mockServer.expect(requestTo("http://localhost:5001/authenticate"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(loginResponse)));

        // Llamamos al método que queremos probar.
        var result = authService.login(loginRequest);

        // Verificamos que el resultado no sea nulo y que el JWT sea el esperado.
        assertNotNull(result);
        assertEquals("fake-jwt-token", result.getJwt());
    }

    @Test
    void register_ShouldReturnRegisterResponse_WhenCalled() throws JsonProcessingException {
        // Preparamos los datos de prueba.
        var registerRequest = new RegisterRequest("New", "User", "newuser@example.com", "newpassword", "123 Main St", "123456789", 1);
        var registerResponse = new RegisterResponse();
        registerResponse.setId(1L);

        // Configuramos el servidor de prueba para que espere una petición POST a "/usuarios"
        // y devuelva una respuesta exitosa (200 OK) con el cuerpo de registerResponse en formato JSON.
        mockServer.expect(requestTo("http://localhost:5001/usuarios"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(registerResponse)));

        // Llamamos al método que queremos probar.
        var result = authService.register(registerRequest);

        // Verificamos que el resultado no sea nulo y que el ID sea el esperado.
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }
}
