package danielpm.dev.dto.request.user;

import danielpm.dev.entity.User;
import lombok.Getter;
import lombok.Setter;

/**
 * @author danielpm.dev
 */
@Getter
@Setter
public class FullUserDTO {
    private Long id;
    private String username;
    private String password;
    private String email;

    // Constructor vacío para deserialización
    public FullUserDTO() {}

    // Constructor para mapear desde la entidad User
    public FullUserDTO(User owner) {
        this.id = owner.getId();
        this.username = owner.getUsername();
        this.password = owner.getPassword(); // Nota: normalmente no se incluye en respuestas
        this.email = owner.getEmail();
    }
}
