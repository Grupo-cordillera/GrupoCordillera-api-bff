package api.bff.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Este es un DTO (Data Transfer Object).
 * Su única función es ser una "caja" para transportar los datos que envía el Frontend
 * hacia nuestro BFF. En este caso, recibe las credenciales del usuario.
 */
@Data // Lombok: Nos genera automáticamente los Getters, Setters, toString, etc.
@NoArgsConstructor // Lombok: Constructor vacío
@AllArgsConstructor // Lombok: Constructor con todos los argumentos
public class LoginRequest {
    private String username;
    private String password;
}
