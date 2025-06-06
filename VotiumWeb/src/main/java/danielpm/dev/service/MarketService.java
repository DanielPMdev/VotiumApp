package danielpm.dev.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import danielpm.dev.dto.response.event.ResponseEventDTO;
import danielpm.dev.model.Event;
import danielpm.dev.model.Market;
import danielpm.dev.model.Option;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.yaml.snakeyaml.error.Mark;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author danielpm.dev
 */
@Service
public class MarketService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${api.base-url}")
    private String apiBaseUrl;

    public MarketService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public List<Market> getMarkets(String token, Long eventId) {
        String url = apiBaseUrl + "/api/event/" + eventId + "/markets";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.trim());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<List<Market>> response = restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<Market>>() {});
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                throw new RuntimeException("Error al obtener los mercados: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error al obtener los mercados: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    public Market getMarketById(String token, Long id) {
        String url = apiBaseUrl + "/api/market/" + id;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.trim());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Market> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Market.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new RuntimeException("Error al obtener el mercado: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error al obtener el mercado: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    public void saveMarketWithOptions(String token, Long eventId, Market market, List<Option> options) {
        String url = apiBaseUrl + "/api/marketWithOptions";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.trim());
        headers.setContentType(MediaType.APPLICATION_JSON);

        StringBuilder jsonBody = new StringBuilder("{");

        jsonBody.append("\"question\": \"").append(market.getQuestion()).append("\",");

        jsonBody.append("\"eventId\": ").append(eventId).append(",");

        jsonBody.append("\"options\":[");

        String optionsJson = options.stream()
                .map(option -> {
                    String percentage = option.getPercentage() != null ? option.getPercentage().toString() : "null";
                    return String.format("{\"text\": \"%s\", \"percentage\": %s, \"winner\": %b}",
                            option.getText(), percentage, option.getIsWinner());
                })
                .collect(Collectors.joining(","));

        jsonBody.append(optionsJson).append("]");

        jsonBody.append("}");

        System.out.println("JSON enviado:\n" + jsonBody);

        HttpEntity<String> entity = new HttpEntity<>(jsonBody.toString(), headers);

        try {
            ResponseEntity<Market> response = restTemplate.exchange(url, HttpMethod.POST, entity, Market.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Error al crear el evento: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error al crear el evento: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    public void updateMarket(String token, Long eventId, Market market) {
        String url = apiBaseUrl + "/api/market/" + market.getId();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.trim());
        headers.setContentType(MediaType.APPLICATION_JSON);

        StringBuilder jsonBody = new StringBuilder("{");

        jsonBody.append("\"id\": ").append(market.getId()).append(",");
        jsonBody.append("\"question\": \"").append(market.getQuestion()).append("\",");
        jsonBody.append("\"eventId\": ").append(eventId);

        jsonBody.append("}");

        System.out.println("JSON enviado (update):\n" + jsonBody);

        HttpEntity<String> entity = new HttpEntity<>(jsonBody.toString(), headers);

        try {
            ResponseEntity<Market> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Market.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Error al actualizar el mercado: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error al actualizar el mercado: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

//    public void updateMarketWithOptions(String token, Long eventId, Market market, List<Option> options) {
//        String url = apiBaseUrl + "/api/marketWithOptions/" + market.getId();
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer " + token.trim());
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        StringBuilder jsonBody = new StringBuilder("{");
//
//        jsonBody.append("\"id\": ").append(market.getId()).append(",");
//        jsonBody.append("\"question\": \"").append(market.getQuestion()).append("\",");
//        jsonBody.append("\"eventId\": ").append(eventId).append(",");
//        jsonBody.append("\"options\":[");
//
//        String optionsJson = options.stream()
//                .map(option -> {
//                    String percentage = option.getPercentage() != null ? option.getPercentage().toString() : "null";
//                    String optionId = option.getId() != null ? "\"id\": " + option.getId() + "," : "";
//                    return String.format("{%s\"text\": \"%s\", \"percentage\": %s, \"winner\": %b}",
//                            optionId, option.getText(), percentage, option.getIsWinner());
//                })
//                .collect(Collectors.joining(","));
//
//        jsonBody.append(optionsJson).append("]}");
//
//        System.out.println("JSON enviado (update):\n" + jsonBody);
//
//        HttpEntity<String> entity = new HttpEntity<>(jsonBody.toString(), headers);
//
//        try {
//            ResponseEntity<Market> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Market.class);
//            if (response.getStatusCode() != HttpStatus.OK) {
//                throw new RuntimeException("Error al actualizar el mercado: " + response.getStatusCode());
//            }
//        } catch (HttpClientErrorException e) {
//            throw new RuntimeException("Error al actualizar el mercado: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
//        }
//    }

    public void deleteMarketById(String token, Long marketId) {
        String url = apiBaseUrl + "/api/market/" + marketId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.trim());

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
            if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                // Éxito, no hay cuerpo esperado
            } else {
                throw new RuntimeException("Error al eliminar el mercado: " + response.getStatusCode() + " - " + response.getBody());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error al eliminar el mercado: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

}
