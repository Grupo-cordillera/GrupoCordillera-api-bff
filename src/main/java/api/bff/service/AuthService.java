package api.bff.service;

import api.bff.dto.auth.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Los "Services" en Spring Boot son las clases encargadas de la LÓGICA DE NEGOCIO.
 * En el contexto del BFF, esta clase es la responsable de conectarse
 * físicamente con tus otros microservicios (en este caso, Auth).
 */
@Service
public class AuthService {

    // Este es el cliente HTTP que configuramos en RestClientConfig.java
    // Nos permite hacer peticiones (GET, POST, etc.) de forma sencilla.
    private final RestClient authRestClient;

    // Usamos @Qualifier para decirle a Spring exactamente cuál RestClient inyectar,
    // ya que tenemos dos: "authRestClient" e "inventoryRestClient".
    public AuthService(@Qualifier("authRestClient") RestClient authRestClient) {
        this.authRestClient = authRestClient;
    }

    /**
     * Este método recibe las credenciales del usuario desde el controlador,
     * las envía al microservicio de Auth y devuelve la respuesta.
     */
    @CircuitBreaker(name = "authService", fallbackMethod = "loginFallback")
    public LoginResponse login(LoginRequest loginRequest) {
        // Hacemos una petición HTTP internamente
        return authRestClient.post() // 1. Indicamos que es una petición POST
                .uri("/authenticate") // 2. La ruta en el microservicio destino (localhost:5001/authenticate)
                .contentType(MediaType.APPLICATION_JSON) // 3. Le decimos que le enviamos un JSON
                .body(loginRequest) // 4. Le pasamos el cuerpo (username y password)
                .retrieve() // 5. Ejecutamos la petición y obtenemos la respuesta
                .body(LoginResponse.class); // 6. Convertimos el JSON de respuesta en nuestra clase LoginResponse
    }

    /**
     * Este método envía los datos de registro al microservicio de Auth
     * para crear un nuevo usuario.
     */
    @CircuitBreaker(name = "authService", fallbackMethod = "registerFallback")
    public RegisterResponse register(RegisterRequest registerRequest) {
        return authRestClient.post() // Petición POST
                .uri("/usuarios") // Ruta en tu microservicio Auth para crear usuarios
                .contentType(MediaType.APPLICATION_JSON)
                .body(registerRequest)
                .retrieve()
                .body(RegisterResponse.class);
    }

    /**
     * Obtiene la lista de todos los usuarios desde el microservicio Auth.
     */
    @CircuitBreaker(name = "authService", fallbackMethod = "getAllUsersFallback")
    public List<UserResponse> getAllUsers() {
        return authRestClient.get() // Petición GET
                .uri("/usuarios") // Ruta en tu microservicio Auth
                .retrieve()
                // Usamos ParameterizedTypeReference porque estamos esperando una Lista (List<T>)
                .body(new ParameterizedTypeReference<List<UserResponse>>() {});
    }

    /**
     * Obtiene la lista de todos los roles desde el microservicio Auth.
     */
    @CircuitBreaker(name = "authService", fallbackMethod = "getAllRolesFallback")
    public List<RolResponse> getAllRoles() {
        return authRestClient.get() // Petición GET
                .uri("/api/rol") // Ruta correcta en tu microservicio Auth
                .retrieve()
                .body(new ParameterizedTypeReference<List<RolResponse>>() {});
    }

    /**
     * Este método envía la nueva contraseña al microservicio de Auth
     * para actualizar un usuario existente.
     */
    @CircuitBreaker(name = "authService", fallbackMethod = "changePasswordFallback")
    public ChangePasswordResponse changePassword(Long id, ChangePasswordRequest changePasswordRequest) {
        return authRestClient.patch()
                .uri("/usuarios/{id}/change-password", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(changePasswordRequest)
                .retrieve()
                .body(ChangePasswordResponse.class);
    }

    /**
     * Este método envía los datos actualizados de un usuario al microservicio de Auth.
     */
    @CircuitBreaker(name = "authService", fallbackMethod = "updateUserFallback")
    public UpdateUserResponse updateUser(Long id, UpdateUserRequest updateUserRequest) {
        return authRestClient.put()
                .uri("/usuarios/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(updateUserRequest)
                .retrieve()
                .body(UpdateUserResponse.class);
    }

    /**
     * Este método envía una petición DELETE al microservicio de Auth para eliminar un usuario.
     */
    @CircuitBreaker(name = "authService", fallbackMethod = "deleteUserFallback")
    public void deleteUser(Long id) {
        authRestClient.delete()
                .uri("/usuarios/{id}", id)
                .retrieve()
                .toBodilessEntity(); // Usamos toBodilessEntity() porque el endpoint devuelve ResponseEntity.noContent().build()
    }

    private LoginResponse loginFallback(LoginRequest loginRequest, Throwable ex) {
        throw buildServiceUnavailable("auth", ex);
    }

    private RegisterResponse registerFallback(RegisterRequest registerRequest, Throwable ex) {
        throw buildServiceUnavailable("auth", ex);
    }

    private List<UserResponse> getAllUsersFallback(Throwable ex) {
        throw buildServiceUnavailable("auth", ex);
    }

    private List<RolResponse> getAllRolesFallback(Throwable ex) {
        throw buildServiceUnavailable("auth", ex);
    }

    private ChangePasswordResponse changePasswordFallback(Long id, ChangePasswordRequest changePasswordRequest, Throwable ex) {
        throw buildServiceUnavailable("auth", ex);
    }

    private UpdateUserResponse updateUserFallback(Long id, UpdateUserRequest updateUserRequest, Throwable ex) {
        throw buildServiceUnavailable("auth", ex);
    }

    private void deleteUserFallback(Long id, Throwable ex) {
        throw buildServiceUnavailable("auth", ex);
    }

    private ResponseStatusException buildServiceUnavailable(String serviceName, Throwable ex) {
        return new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Servicio de " + serviceName + " no disponible",
                ex
        );
    }
}
