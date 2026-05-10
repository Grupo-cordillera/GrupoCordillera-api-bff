package api.bff.service;

import api.bff.dto.auth.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

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
    public RegisterResponse register(RegisterRequest registerRequest) {
        return authRestClient.post() // Petición POST
                .uri("/usuarios") // Ruta en tu microservicio Auth para crear usuarios
                .contentType(MediaType.APPLICATION_JSON)
                .body(registerRequest)
                .retrieve()
                .body(RegisterResponse.class);
    }

    /**
     * Este método envía la nueva contraseña al microservicio de Auth
     * para actualizar un usuario existente.
     */
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
    public UpdateUserResponse updateUser(Long id, UpdateUserRequest updateUserRequest) {
        return authRestClient.put()
                .uri("/usuarios/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(updateUserRequest)
                .retrieve()
                .body(UpdateUserResponse.class);
    }
}
