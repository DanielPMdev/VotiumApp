package danielpm.dev.dto.response.event;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author danielpm.dev
 */
@Getter
@Setter
public class PaginatedResponseEventDTO {
    private List<ResponseEventDTO> content;
    private int totalPages;
    private int number; // currentPage
    private int size;
    private long totalElements;

    public PaginatedResponseEventDTO() {
    }
}

