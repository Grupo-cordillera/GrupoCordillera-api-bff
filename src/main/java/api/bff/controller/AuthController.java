package api.bff.controller;

import api.bff.dto.auth.LoginRequest;
import api.bff.dto.auth.LoginResponse;
import api.bff.dto.auth.RegisterRequest;
import api.bff.dto.auth.RegisterResponse;
import api.bff.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Los "Controllers" en Spring Boot son la "puerta de entrada" para tu aplicación.
 * Ellos exponen las URLs que el FRONTEND (React, Angular, Móvil) va a llamar.
 * En el patrón BFF, este controlador recibe la petición del Frontend y se la pasa al Service.
 */
@RestController // Indica que esta clase expone endpoints REST (que devuelven JSON por defecto)
@RequestMapping("/api/bff/auth") // Esta es la ruta base de este controlador. Ej: localhost:8080/api/bff/auth
@RequiredArgsConstructor // Lombok: Genera automáticamente el constructor para inyectar authService
public class AuthController {

    // Inyectamos nuestro servicio (la capa lógica donde nos comunicamos con otros microservicios)
    private final AuthService authService;

    /**
     * Endpoint para iniciar sesión.
     * URL completa: POST http://localhost:8080/api/bff/auth/login
     *
     * @param loginRequest Las credenciales que envía el Frontend en el Body.
     * @return La respuesta (Token + Datos del usuario) en formato JSON.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        // 1. Llamamos a nuestro servicio pasándole los datos del login
        LoginResponse response = authService.login(loginRequest);
        
        // 2. Si todo salió bien, devolvemos un HTTP 200 (OK) con la respuesta
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para registrar un nuevo usuario.
     * URL completa: POST http://localhost:8080/api/bff/auth/register
     * 
     * @param registerRequest Los datos del usuario a crear.
     * @return El usuario creado (sin la contraseña por seguridad).
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest registerRequest) {
        RegisterResponse response = authService.register(registerRequest);
        return ResponseEntity.ok(response);
    }
}
