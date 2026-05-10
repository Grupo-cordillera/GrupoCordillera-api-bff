package api.bff.dto.auth;

import lombok.Data;

@Data
public class ChangePasswordResponse {
    private Long id;
    private String nombre;
    private String apellido;
    private String correo;
    private String direccion;
    private String telefono;
    private RolResponse rol;
}
