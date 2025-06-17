package danielpm.dev.dto.response.user;

import danielpm.dev.model.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author danielpm.dev
 */
@Getter
@Setter
public class PaginatedResponseUserDTO {
    private List<User> content;
    private int totalPages;
    private int number;
    private long totalElements;
    private int size;
    private boolean first;
    private boolean last;

    public PaginatedResponseUserDTO() {
    }
}
