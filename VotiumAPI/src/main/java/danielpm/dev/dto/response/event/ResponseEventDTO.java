package danielpm.dev.dto.response.event;

import danielpm.dev.dto.response.market.MarketQuestionDTO;
import danielpm.dev.entity.Event;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    // Opcional: Si quieres meter info resumida del Market
    private List<MarketQuestionDTO> markets;

    public ResponseEventDTO() {
    }

    // Constructor
    public ResponseEventDTO(Event event) {
        this.id = event.getId();
        this.title = event.getTitle();
        this.description = event.getDescription();
        this.createdAt = event.getCreatedAt();
        this.closedAt = event.getClosedAt();
        this.closedEvent = event.isClosedEvent();
        this.urlImage = event.getUrlImage();

        this.categoryId = event.getCategory() != null ? event.getCategory().getId() : null;
        this.categoryName = event.getCategory() != null ? event.getCategory().getName() : null;

        this.userId = event.getUser() != null ? event.getUser().getId() : null;
        this.username = event.getUser() != null ? event.getUser().getUsername() : null;

        // Si quieres incluir info de Markets resumida:
        this.markets = event.getMarketList() != null
                ? event.getMarketList().stream()
                .map(MarketQuestionDTO::new)
                .collect(Collectors.toList())
                : List.of(); // Evita null
    }
}
