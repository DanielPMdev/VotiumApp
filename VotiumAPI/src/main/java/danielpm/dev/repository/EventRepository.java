package danielpm.dev.repository;

import danielpm.dev.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author danielpm.dev@gmail.com
 */
public interface EventRepository extends JpaRepository<Event, Long> {

    Optional<List<Event>> findAllByTitle(String title);

    Optional<List<Event>> findAllByCreatedAt(LocalDate createdAt);

    Optional<List<Event>> findAllByClosedAt(LocalDateTime closedAt);

    Optional<List<Event>> findAllByClosedEvent(boolean closedEvent);

    List<Event> findAllByClosedAtBeforeAndClosedEventFalse(LocalDateTime closedAtBefore);
}