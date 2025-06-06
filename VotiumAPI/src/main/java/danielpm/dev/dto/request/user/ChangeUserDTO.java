package danielpm.dev.dto.request.user;

import danielpm.dev.entity.Role;
import lombok.Getter;
import lombok.Setter;

/**
 * @author danielpm.dev
 */
@Getter
@Setter
public class ChangeUserDTO {

    private Long id;
    private String email;
    private String password;

    private Role role;

    public ChangeUserDTO() {
    }

    public ChangeUserDTO(Long id, String email, String password, Role role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
