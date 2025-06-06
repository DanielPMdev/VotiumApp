package danielpm.dev.dto.request.auth;

import lombok.Getter;
import lombok.Setter;

/**
 * @author danielpm.dev
 */
@Getter
@Setter
public class RegisterRequest {

    private String username;
    private String password;
    private String email;

    public RegisterRequest() {
    }

    public RegisterRequest(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
}
