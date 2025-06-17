package danielpm.dev.dto.response.user;


import danielpm.dev.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

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

    public PaginatedResponseUserDTO(Page<User> page) {
        this.content = page.getContent();
        this.totalPages = page.getTotalPages();
        this.number = page.getNumber();
        this.totalElements = page.getTotalElements();
        this.size = page.getSize();
        this.first = page.isFirst();
        this.last = page.isLast();
    }

}
