package api.bff.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la petición de registro de usuario.
 * Coincide con los datos que espera el microservicio de Auth (UsuarioRequest).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String nombre;
    private String apellido;
    private String correo;
    private String contrasena;
    private String direccion;
    private String telefono;
    private Integer numero_rol;
}
