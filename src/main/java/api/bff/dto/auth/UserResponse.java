package api.bff.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para representar a un usuario en el frontend.
 * Lo usaremos para listar los usuarios. Excluimos la contraseña por seguridad.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String nombre;
    private String apellido;
    private String correo;
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
