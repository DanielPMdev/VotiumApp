package danielpm.dev.dto.response.event;


import danielpm.dev.model.Event;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author danielpm.dev
 */
@Getter
@Setter
public class CloseEventDTO {

    private Long id;

    private LocalDateTime closedAt;

    private boolean isClosed;

    public CloseEventDTO() {
    }

    public CloseEventDTO(Event event) {
        this.id = event.getId();
        this.closedAt = event.getClosedAt();
        this.isClosed = event.isClosedEvent();
    }
}
