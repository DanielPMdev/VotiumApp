package danielpm.dev.service.impl;

import danielpm.dev.entity.Event;
import danielpm.dev.repository.EventRepository;
import danielpm.dev.service.EventService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author danielpm.dev
 */
@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public boolean existsById(Long id) {
        return eventRepository.existsById(id);
    }

    @Override
    public List<Event> getAllEvents() {
        closeExpiredEvents();
        return eventRepository.findAll();
    }

    @Override
    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    @Override
    public Optional<List<Event>> getEventByTitle(String title) {
        return eventRepository.findAllByTitle(title);
    }

    @Override
    public Optional<List<Event>> getEventByCreatedAt(LocalDate createDate) {
        return eventRepository.findAllByCreatedAt(createDate);
    }

    @Override
    public Optional<List<Event>> getEventByClosedAt(LocalDateTime closedDate) {
        return eventRepository.findAllByClosedAt(closedDate);
    }

    @Override
    public Optional<List<Event>> getEventByIsClosed(boolean isClosed) {
        return eventRepository.findAllByClosedEvent(isClosed);
    }

    @Override
    public Event createOrUpdateEvent(Event event) {
        eventRepository.save(event);
        return event;
    }

    @Override
    public void deleteEventById(Long id) {
        eventRepository.deleteById(id);
    }

    @Override
    public void deleteAllEvents() {
        eventRepository.deleteAll();
    }

    @Override
    public void closeExpiredEvents() {
        LocalDateTime now = LocalDateTime.now();

        List<Event> toUpdate = eventRepository.findAllByClosedAtBeforeAndClosedEventFalse(now);

        toUpdate.forEach(event -> event.setClosedEvent(true));

        if (!toUpdate.isEmpty()) {
            eventRepository.saveAll(toUpdate);
        }
    }

}
