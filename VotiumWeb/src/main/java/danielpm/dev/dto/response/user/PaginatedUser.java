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
public class PaginatedUser {
    private List<User> users;
    private int totalPages;
    private int currentPage;

    public PaginatedUser(List<User> users, int totalPages, int currentPage) {
        this.users = users;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
    }
}

