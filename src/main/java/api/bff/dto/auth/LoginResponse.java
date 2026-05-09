package api.bff.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Este DTO representa la respuesta que el BFF le va a enviar al Frontend.
 * Lo hemos actualizado para que coincida exactamente con la nueva respuesta
 * de tu microservicio de Auth, añadiendo los datos del usuario además del token.
 */
@Data // Lombok: Genera Getters y Setters
@NoArgsConstructor // Lombok: Constructor vacío (necesario para que RestClient pueda mapear el JSON)
@AllArgsConstructor // Lombok: Constructor con todos los campos
public class LoginResponse {
    private String jwt;
    private String nombre;
    private String correo;
    private String direccion;
    private String telefono;
    private String rol;
}
