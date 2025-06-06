package danielpm.dev.controller;

import danielpm.dev.dto.request.event.ChangeEventDTO;
import danielpm.dev.dto.request.event.CloseEventDTO;
import danielpm.dev.dto.request.event.CreateEventDTO;
import danielpm.dev.dto.request.image.ImageUpdateRequest;
import danielpm.dev.dto.response.event.ResponseEventDTO;
import danielpm.dev.entity.Category;
import danielpm.dev.entity.Event;
import danielpm.dev.entity.Market;
import danielpm.dev.entity.User;
import danielpm.dev.service.CategoryService;
import danielpm.dev.service.EventService;
import danielpm.dev.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author danielpm.dev
 */
@RestController
@RequestMapping("/api")
public class EventController {

    @Value("${event.default.image}")
    private String defaultImage;

    private final EventService eventService;
    private final UserService userService;
    private final CategoryService categoryService;

    public EventController(EventService eventService, UserService userService, CategoryService categoryService) {
        this.eventService = eventService;
        this.userService = userService;
        this.categoryService = categoryService;
    }

    /*
        GET http://localhost:8080/api/events
     */
    @GetMapping("/events")
    public ResponseEntity<List<ResponseEventDTO>> findAll() {
        List<Event> eventList = eventService.getAllEvents();

        if (eventList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<ResponseEventDTO> responseEventDTOList = eventList.stream()
                .map(ResponseEventDTO::new).toList();

        return ResponseEntity.ok(responseEventDTOList);
    }

    /*
        GET http://localhost:8080/api/event/7
     */
    @GetMapping("/event/{id}")
    public ResponseEntity<ResponseEventDTO> findById(@PathVariable Long id) {
        if (id < 0) {
            return ResponseEntity.badRequest().build();
        }

        return eventService.getEventById(id)
                .map(event -> ResponseEntity.ok(new ResponseEventDTO(event)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /*
        GET http://localhost:8080/api/event/7/markets
     */
    @GetMapping("/event/{id}/markets")
    public ResponseEntity<List<Market>> findAllMarkets(@PathVariable Long id) {
        if (id < 0) {
            return ResponseEntity.badRequest().build();
        }

        return eventService.getEventById(id)
                .map(event -> ResponseEntity.ok(event.getMarketList()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    

    /*
        POST http://localhost:8080/api/event
     */
    @PostMapping("/event")
    public ResponseEntity<Event> createEvent(@RequestBody CreateEventDTO eventDTO) {
        if (eventDTO.getId() != null) //Si ya tiene un id asignado
            return ResponseEntity.badRequest().build();

        //Buscar el User mediante el ID
        User user = userService.getUserById(eventDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = categoryService.getCategoryById(eventDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Event eventCreated = getEventCreated(eventDTO, user, category);

        eventService.createOrUpdateEvent(eventCreated);
        return ResponseEntity.ok(eventCreated);
    }

    /*
        PUT http://localhost:8080/api/event/3
     */
    @PutMapping("/event/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody ChangeEventDTO eventDTO) {
        if (!eventService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        //Obtener el Evento existente mediante el ID
        Event eventToUpdate = eventService.getEventById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        eventToUpdate.setTitle(eventDTO.getTitle());
        eventToUpdate.setDescription(eventDTO.getDescription());
        eventToUpdate.setCreatedAt(eventDTO.getCreatedAt());
        eventToUpdate.setClosedAt(eventDTO.getClosedAt());
        eventToUpdate.setClosedEvent(eventDTO.isClosed());
        eventToUpdate.setUrlImage(eventDTO.getUrlImage() == null ? defaultImage : eventDTO.getUrlImage());

        // Actualizar la categoría
        if (eventDTO.getCategoryId() != null) {
            Category category = categoryService.getCategoryById(eventDTO.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            eventToUpdate.setCategory(category);
        }

        Event updatedEvent = eventService.createOrUpdateEvent(eventToUpdate);
        return ResponseEntity.ok(updatedEvent);
    }

    /*
        PUT http://localhost:8080/api/event/image/3
     */
    @PutMapping("/event/image/{id}")
    public ResponseEntity<Event> updateImageEvent(@PathVariable Long id, @RequestBody ImageUpdateRequest imgRequest) {
        if (!eventService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        //Obtener el Evento existente mediante el ID
        Event eventToUpdate = eventService.getEventById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if(imgRequest.getImageUrl() != null && !imgRequest.getImageUrl().isEmpty()) {
            eventToUpdate.setUrlImage(imgRequest.getImageUrl());

            Event updatedEvent = eventService.createOrUpdateEvent(eventToUpdate);
            return ResponseEntity.ok(updatedEvent);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    /*
        PUT http://localhost:8080/api/event/close/3
     */
    @PutMapping("/event/close/{id}")
    public ResponseEntity<Event> closeEvent(@PathVariable Long id, @RequestBody CloseEventDTO closeEventDTO) {
        if (!eventService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        //Obtener el Evento existente mediante el ID
        Event eventToUpdate = eventService.getEventById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if(closeEventDTO.getClosedAt() != null) {
            eventToUpdate.setClosedAt(closeEventDTO.getClosedAt());

            eventToUpdate.setClosedEvent(closeEventDTO.isClosed());

            Event updatedEvent = eventService.createOrUpdateEvent(eventToUpdate);
            return ResponseEntity.ok(updatedEvent);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    /*
        DELETE http://localhost:8080/api/event/{identifier}
     */
    @DeleteMapping("/event/{identifier}")
    public ResponseEntity<Void> deleteEventById(@PathVariable("identifier") Long id) {
        if (!eventService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        eventService.deleteEventById(id);
        return ResponseEntity.noContent().build();
    }


    //AUXILIARY METHODS

    private Event getEventCreated(CreateEventDTO eventDTO, User user, Category category) {
        Event eventCreated = new Event();

        eventCreated.setId(eventDTO.getId());
        eventCreated.setTitle(eventDTO.getTitle());
        eventCreated.setDescription(eventDTO.getDescription());
        eventCreated.setCreatedAt(eventDTO.getCreatedAt());
        eventCreated.setClosedAt(eventDTO.getClosedAt());
        eventCreated.setClosedEvent(eventDTO.isClosed());
        eventCreated.setUrlImage(eventDTO.getUrlImage() == null ? defaultImage : eventDTO.getUrlImage());

        eventCreated.setUser(user);
        eventCreated.setCategory(category);
        return eventCreated;
    }
}
