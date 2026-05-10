package api.bff.dto.auth;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private String newPassword;
}
