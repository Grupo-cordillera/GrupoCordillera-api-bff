package api.bff.controller;

import api.bff.dto.auth.*;
import api.bff.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        
        // 2. Si salió bien, devolvemos un HTTP 200 (OK) con la respuesta
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

    /**
     * Endpoint para listar los usuarios.
     * URL completa: GET http://localhost:8080/api/bff/auth/usuarios
     *
     * @return Una lista de usuarios.
     */
    @GetMapping("/usuarios")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = authService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Endpoint para listar los roles.
     * URL completa: GET http://localhost:8080/api/bff/auth/roles
     *
     * @return Una lista de roles.
     */
    @GetMapping("/roles")
    public ResponseEntity<List<RolResponse>> getAllRoles() {
        List<RolResponse> roles = authService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    /**
     * Endpoint para cambiar la contraseña de un usuario.
     * URL completa: PATCH http://localhost:8080/api/bff/auth/usuarios/{id}/change-password
     *
     * @param id El ID del usuario a modificar.
     * @param changePasswordRequest El cuerpo de la petición con la nueva contraseña.
     * @return El usuario actualizado.
     */
    @PatchMapping("/usuarios/{id}/change-password")
    public ResponseEntity<ChangePasswordResponse> changePassword(@PathVariable Long id, @RequestBody ChangePasswordRequest changePasswordRequest) {
        ChangePasswordResponse response = authService.changePassword(id, changePasswordRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para actualizar los datos de un usuario.
     * URL completa: PUT http://localhost:8080/api/bff/auth/usuarios/{id}
     *
     * @param id El ID del usuario a modificar.
     * @param updateUserRequest El cuerpo de la petición con los nuevos datos del usuario.
     * @return El usuario actualizado.
     */
    @PutMapping("/usuarios/{id}")
    public ResponseEntity<UpdateUserResponse> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest updateUserRequest) {
        UpdateUserResponse response = authService.updateUser(id, updateUserRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para eliminar un usuario.
     * URL completa: DELETE http://localhost:8080/api/bff/auth/usuarios/{id}
     *
     * @param id El ID del usuario a eliminar.
     * @return Un código de estado 204 No Content si se eliminó correctamente.
     */
    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        authService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
