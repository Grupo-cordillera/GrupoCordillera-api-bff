package api.bff.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RolResponse {
    private Long id;
    private Integer numeroRol;
    private String nombre;
    private String funcion;
}
