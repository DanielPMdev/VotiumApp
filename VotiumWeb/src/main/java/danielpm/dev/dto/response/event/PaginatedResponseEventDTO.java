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
    private int number;
    private long totalElements;
    private int size;
    private boolean first;
    private boolean last;

    public PaginatedResponseEventDTO() {
    }
}

