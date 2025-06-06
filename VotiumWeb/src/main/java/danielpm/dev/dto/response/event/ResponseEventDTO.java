package danielpm.dev.dto.response.event;

import danielpm.dev.model.Market;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author danielpm.dev
 */
@Getter
@Setter
public class ResponseEventDTO {

    private Long id;
    private String title;
    private String description;
    private LocalDate createdAt;
    private LocalDateTime closedAt;
    private boolean closedEvent;
    private String urlImage;

    private Long categoryId;
    private String categoryName;

    private Long userId;
    private String username;

    private List<Market> markets;


    public ResponseEventDTO() {}
}

