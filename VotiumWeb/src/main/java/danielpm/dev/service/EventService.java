package danielpm.dev.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import danielpm.dev.dto.response.event.CloseEventDTO;
import danielpm.dev.dto.response.event.PaginatedEvent;
import danielpm.dev.dto.response.event.PaginatedResponseEventDTO;
import danielpm.dev.dto.response.event.ResponseEventDTO;
import danielpm.dev.model.Category;
import danielpm.dev.model.Event;
import danielpm.dev.model.Market;
import danielpm.dev.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author danielpm.dev
 */
@Service
public class EventService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${api.base-url}")
    private String apiBaseUrl;

    public EventService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public PaginatedEvent getAllEvents(String token, int page, int size) {
        String url = apiBaseUrl + "/api/events?page=" + page + "&size=" + size;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.trim());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<PaginatedResponseEventDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<PaginatedResponseEventDTO>() {}
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                PaginatedResponseEventDTO body = response.getBody();
                List<Event> events = body.getContent().stream()
                        .map(this::fromDTO)
                        .collect(Collectors.toList());

                return new PaginatedEvent(events, body.getTotalPages(), body.getNumber());
            } else {
                throw new RuntimeException("Error al obtener los eventos: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error al obtener los eventos: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    public Event getEventById(String token, Long id) {
        String url = apiBaseUrl + "/api/event/" + id;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.trim());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<ResponseEventDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    ResponseEventDTO.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return fromDTO(response.getBody());
            } else {
                throw new RuntimeException("Error al obtener el evento: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error al obtener el evento: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }


    public void addEvent(String token, Event event, Long userId) {
        String url = apiBaseUrl + "/api/event";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.trim());
        headers.setContentType(MediaType.APPLICATION_JSON);

        StringBuilder jsonBody = new StringBuilder("{");

        jsonBody.append("\"title\": \"").append(event.getTitle()).append("\",");

        if (event.getDescription() != null) {
            jsonBody.append("\"description\": \"").append(event.getDescription()).append("\",");
        }

        // Asegúrate de convertir LocalDateTime a texto
        jsonBody.append("\"createdAt\": \"").append(LocalDateTime.now()).append("\",");

        if (event.getClosedAt() != null) {
            jsonBody.append("\"closedAt\": \"").append(event.getClosedAt().toString()).append("\",");
        }

        jsonBody.append("\"closed\": false,");

        if (event.getUrlImage() != null && !event.getUrlImage().isBlank()) {
            jsonBody.append("\"urlImage\": \"").append(event.getUrlImage()).append("\",");
        }

        jsonBody.append("\"categoryId\": ").append(event.getCategory().getId()).append(",");

        jsonBody.append("\"userId\": ").append(userId);

        jsonBody.append("}");

        System.out.println("JSON enviado:\n" + jsonBody);

        HttpEntity<String> entity = new HttpEntity<>(jsonBody.toString(), headers);

        try {
            ResponseEntity<Event> response = restTemplate.exchange(url, HttpMethod.POST, entity, Event.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Error al crear el evento: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error al crear el evento: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    public void updateEvent(String token, Event event, Long userId) {
        String url = apiBaseUrl + "/api/event/" + event.getId();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.trim());
        headers.setContentType(MediaType.APPLICATION_JSON);

        DateTimeFormatter formatterDate = DateTimeFormatter.ISO_LOCAL_DATE;
        DateTimeFormatter formatterTime = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        StringBuilder jsonBody = new StringBuilder("{");

        jsonBody.append("\"id\": \"").append(event.getId()).append("\",");

        jsonBody.append("\"title\": \"").append(event.getTitle()).append("\",");

        if (event.getDescription() != null) {
            jsonBody.append("\"description\": \"").append(event.getDescription()).append("\",");
        }

        jsonBody.append("\"createdAt\": \"").append(event.getCreatedAt().format(formatterDate)).append("\",");

        if (event.getClosedAt() != null) {
            jsonBody.append("\"closedAt\": \"").append(event.getClosedAt().format(formatterTime)).append("\",");
        }

        jsonBody.append("\"closed\": ").append(event.isClosedEvent()).append(",");

        if (event.getUrlImage() != null && !event.getUrlImage().isBlank()) {
            jsonBody.append("\"urlImage\": \"").append(event.getUrlImage()).append("\",");
        }

        jsonBody.append("\"categoryId\": ").append(event.getCategory().getId()).append(",");

        jsonBody.append("\"userId\": ").append(userId);

        jsonBody.append("}");

        System.out.println("JSON enviado:\n" + jsonBody);

        HttpEntity<String> entity = new HttpEntity<>(jsonBody.toString(), headers);

        try {
            ResponseEntity<Event> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Event.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Error al actualizar el evento: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error al actualizar el evento: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    public void closeEventById(String token, Long eventId) {
        String url = apiBaseUrl + "/api/event/close/" + eventId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.trim());
        headers.setContentType(MediaType.APPLICATION_JSON);

        CloseEventDTO closeEventDTO = new CloseEventDTO();
        closeEventDTO.setClosed(true);
        closeEventDTO.setClosedAt(LocalDateTime.now());

        HttpEntity<CloseEventDTO> entity = new HttpEntity<>(closeEventDTO, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                // Success, event closed
            } else {
                throw new RuntimeException("Error al cerrar el evento: " + response.getStatusCode() + " - " + response.getBody());
            }
        } catch (HttpClientErrorException e) {
            System.out.println("Error de la API: " + e.getResponseBodyAsString()); // Debugging
            throw new RuntimeException("Error al cerrar el evento: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    public void deleteEventById(String token, Long eventId) {
        String url = apiBaseUrl + "/api/event/" + eventId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.trim());

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
            if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                // Éxito, no hay cuerpo esperado
            } else {
                throw new RuntimeException("Error al eliminar el evento: " + response.getStatusCode() + " - " + response.getBody());
            }
        } catch (HttpClientErrorException e) {
            System.out.println("Error de la API: " + e.getResponseBodyAsString()); // Depuración
            throw new RuntimeException("Error al eliminar el evento: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    //METHODS AUX
    private Event fromDTO(ResponseEventDTO dto) {
        if (dto == null) return null;

        Event event = new Event();
        event.setId(dto.getId());
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setCreatedAt(dto.getCreatedAt());
        event.setClosedAt(dto.getClosedAt());
        event.setClosedEvent(dto.isClosedEvent());
        event.setUrlImage(dto.getUrlImage());

        // Mapeo de la categoría si está presente
        if (dto.getCategoryName() != null) {
            Category category = new Category();
            category.setId(dto.getCategoryId());
            category.setName(dto.getCategoryName());
            event.setCategory(category);
        }

        // Mapeo del usuario si está presente
        if (dto.getUsername() != null) {
            User user = new User();
            user.setId(dto.getUserId());
            user.setUsername(dto.getUsername());
            event.setUser(user);
        }

        if (dto.getMarkets() != null) {
            event.setMarketList(
                    dto.getMarkets().stream()
                            .map(market -> {
                                Market m = new Market();
                                m.setId(market.getId());
                                m.setQuestion(market.getQuestion());
                                return m;
                            })
                            .collect(Collectors.toList())
            );
        }

        return event;
    }
}
