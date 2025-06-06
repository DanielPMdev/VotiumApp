package danielpm.dev.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import danielpm.dev.dto.response.option.ResponseOptionDTO;
import danielpm.dev.model.Event;
import danielpm.dev.model.Market;
import danielpm.dev.model.Option;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author danielpm.dev
 */
@Service
public class OptionService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${api.base-url}")
    private String apiBaseUrl;

    public OptionService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public List<Option> getOptions(String token, Long marketId) {
        String url = apiBaseUrl + "/api/market/" + marketId + "/options";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.trim());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<List<Option>> response = restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<Option>>() {});
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                throw new RuntimeException("Error al obtener las opciones: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error al obtener las opciones: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    public Option getOptionById(String token, Long id) {
        String url = apiBaseUrl + "/api/option/" + id;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.trim());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<ResponseOptionDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    ResponseOptionDTO.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return fromDTO(response.getBody());
            } else {
                throw new RuntimeException("Error al obtener la opción: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error al obtener la opción: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    public void saveOption(String token, Long marketId, Option option) {
        String url = apiBaseUrl + "/api/option";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.trim());
        headers.setContentType(MediaType.APPLICATION_JSON);

        StringBuilder jsonBody = new StringBuilder("{");

        jsonBody.append("\"text\": \"").append(option.getText()).append("\",");

        jsonBody.append("\"marketId\": ").append(marketId);

        jsonBody.append("}");

        System.out.println("JSON enviado:\n" + jsonBody);

        HttpEntity<String> entity = new HttpEntity<>(jsonBody.toString(), headers);

        try {
            ResponseEntity<Option> response = restTemplate.exchange(url, HttpMethod.POST, entity, Option.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Error al crear la opcion: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error al crear la opcion: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    public void updateOption(String token, Long marketId, Option option) {
        String url = apiBaseUrl + "/api/option/" + option.getId();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.trim());
        headers.setContentType(MediaType.APPLICATION_JSON);

        StringBuilder jsonBody = new StringBuilder("{");

        jsonBody.append("\"id\": ").append(option.getId()).append(",");
        jsonBody.append("\"text\": \"").append(option.getText()).append("\",");
        jsonBody.append("\"marketId\": ").append(marketId);

        jsonBody.append("}");

        System.out.println("JSON enviado (update):\n" + jsonBody);

        HttpEntity<String> entity = new HttpEntity<>(jsonBody.toString(), headers);

        try {
            ResponseEntity<Option> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Option.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Error al actualizar la opción: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error al actualizar la opción: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    public void deleteOptionById(String token, Long optionId) {
        String url = apiBaseUrl + "/api/option/" + optionId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.trim());

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
            if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                // Éxito, no hay cuerpo esperado
            } else {
                throw new RuntimeException("Error al eliminar la opción: " + response.getStatusCode() + " - " + response.getBody());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error al eliminar la opción: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    public void setOptionAsWinner(String token, Long marketId, Long optionId) {
        String url = apiBaseUrl + "/api/option/" + optionId + "/winner";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.trim());
        headers.setContentType(MediaType.APPLICATION_JSON);

        StringBuilder jsonBody = new StringBuilder("{");

        jsonBody.append("\"id\": ").append(optionId).append(",");
        jsonBody.append("\"winner\": ").append(true).append(",");
        jsonBody.append("\"marketId\": ").append(marketId);

        jsonBody.append("}");

        System.out.println("JSON enviado (update):\n" + jsonBody);

        HttpEntity<String> entity = new HttpEntity<>(jsonBody.toString(), headers);

        try {
            ResponseEntity<Option> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Option.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Error al marcar como ganadora la opción: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error al marcar como ganadora la opción: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    //METHODS AUX
    private Option fromDTO(ResponseOptionDTO dto) {
        if (dto == null) return null;

        Option option = new Option();
        option.setId(dto.getId());
        option.setText(dto.getText());
        option.setIsWinner(dto.getIsWinner());
        option.setPercentage(dto.getPercentage());

        // Mapeo del mercado si está presente
        if (dto.getMarketId() != null) {
            Market market = new Market();
            market.setId(dto.getMarketId());
            // Mapeo del evento si está presente
            if (dto.getEventId() != null) {
                Event event = new Event();
                event.setId(dto.getEventId());
                market.setEvent(event);
            }
            option.setMarket(market);
        }

        return option;
    }
}

