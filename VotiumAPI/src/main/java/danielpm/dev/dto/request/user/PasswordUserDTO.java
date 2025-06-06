package danielpm.dev.dto.request.user;

import lombok.Getter;
import lombok.Setter;

/**
 * @author danielpm.dev
 */
@Getter
@Setter
public class PasswordUserDTO {

    private Long id;
    private String password;

    public PasswordUserDTO() {
    }

    public PasswordUserDTO(Long id, String password) {
        this.id = id;
        this.password = password;
    }
}
