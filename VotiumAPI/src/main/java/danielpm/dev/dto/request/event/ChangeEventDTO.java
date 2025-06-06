package danielpm.dev.dto.request.event;

import danielpm.dev.entity.Event;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author danielpm.dev
 */
@Getter
@Setter
public class ChangeEventDTO {

    private Long id;

    private String title;
    private String description;
    private LocalDate createdAt;
    private LocalDateTime closedAt;
    private boolean isClosed;
    private String urlImage;

    private Long categoryId;

    // Default constructor
    public ChangeEventDTO() {

    }

    public ChangeEventDTO(Event event) {
        this.id = event.getId();
        this.title = event.getTitle();
        this.description = event.getDescription();
        this.createdAt = event.getCreatedAt();
        this.closedAt = event.getClosedAt();
        this.isClosed = event.isClosedEvent();
        this.urlImage = event.getUrlImage();
        this.categoryId = event.getCategory().getId();
    }

}
