package danielpm.dev.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @author danielpm.dev@gmail.com
 */
@Getter
@Setter
public class Event {

    private Long id;

    private String title;
    private String description;
    private LocalDate createdAt;
    private LocalDateTime closedAt;
    private boolean closedEvent;

    private String urlImage;

    private Category category;
    private User user;

    private List<Market> marketList;

    public Event() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(id, event.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
