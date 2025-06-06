package danielpm.dev.service;

import danielpm.dev.entity.Event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author danielpm.dev
 */
public interface EventService {

    boolean existsById(Long id);

    void closeExpiredEvents();

    //Methods retrive
    List<Event> getAllEvents();

    Optional<Event> getEventById(Long id);

    Optional<List<Event>> getEventByTitle(String title);

    Optional<List<Event>> getEventByCreatedAt(LocalDate createDate);

    Optional<List<Event>> getEventByClosedAt(LocalDateTime closedDate);

    Optional<List<Event>> getEventByIsClosed(boolean isClosed);

    //OTROS POSIBLES METODOS DE BUSQUEDA: USER, CATEGORY...

    //Methods create / update
    Event createOrUpdateEvent(Event event);

    //Methods delete
    void deleteEventById(Long id);

    void deleteAllEvents();
}
