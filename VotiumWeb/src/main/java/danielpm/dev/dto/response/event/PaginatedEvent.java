package danielpm.dev.dto.response.event;

import danielpm.dev.model.Event;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author danielpm.dev
 */
@Getter
@Setter
public class PaginatedEvent {
    private List<Event> events;
    private int totalPages;
    private int currentPage;

    public PaginatedEvent(List<Event> events, int totalPages, int currentPage) {
        this.events = events;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
    }
}
