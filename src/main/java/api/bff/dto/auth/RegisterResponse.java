package api.bff.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la respuesta del registro.
 * Coincide con lo que devuelve el microservicio Auth al crear un Usuario.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {
    private Long id;
    private String nombre;
    private String apellido;
    private String correo;
    // No devolvemos la contraseña al frontend por seguridad
    private String direccion;
    private String telefono;
    
    // Objeto anidado que representa el rol del usuario
    private RolDto rol;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RolDto {
        private Long id;
        private Integer numeroRol;
        private String nombre;
        private String funcion;
    }
}
