package api.bff.dto.auth;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String nombre;
    private String apellido;
    private String correo;
    private String direccion;
    private String telefono;
    private int numero_rol;
}
