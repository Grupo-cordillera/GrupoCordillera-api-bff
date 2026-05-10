package api.bff.dto.auth;

import lombok.Data;

@Data
public class RolResponse {
    private Long id_rol;
    private int numero_rol;
    private String nombre_rol;
}
