package danielpm.dev.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import danielpm.dev.dto.response.bet.ResponseBetDTO;
import danielpm.dev.dto.response.bet.UserBetStats;
import danielpm.dev.dto.response.event.SumaryEventDTO;
import danielpm.dev.dto.response.market.ResponseMarketDTO;
import danielpm.dev.dto.response.option.SumaryOptionDTO;
import danielpm.dev.model.Bet;
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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author danielpm.dev
 */
@Service
public class BetService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${api.base-url}")
    private String apiBaseUrl;

    public BetService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public List<Bet> getBetsByUserAndMarket(String token, Long userId, Long marketId) {
        String url = apiBaseUrl + "/api/bets/by-user-and-market?userId=" + userId + "&marketId=" + marketId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.trim());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<List<Bet>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<Bet>>() {}
            );
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Collections.emptyList();
            } else {
                throw new RuntimeException("Error al obtener apuestas: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error al obtener apuestas: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    public List<Bet> getBetsByUserAndOption(String token, Long userId, Long optionId) {
        String url = apiBaseUrl + "/api/bets/by-user-and-option?userId=" + userId + "&optionId=" + optionId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.trim());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<List<Bet>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<Bet>>() {}
            );
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Collections.emptyList();
            } else {
                throw new RuntimeException("Error al obtener apuestas: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error al obtener apuestas: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    public List<Bet> getUserBets(String token, Long userId) {
        String url = apiBaseUrl + "/api/bet/by-user/" + userId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.trim());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<List<ResponseBetDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<ResponseBetDTO>>() {}
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody().stream()
                        .map(this::fromDTO)
                        .collect(Collectors.toList());
            } else {
                throw new RuntimeException("Error al obtener apuestas: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Collections.emptyList();
            }
            throw new RuntimeException("Error al obtener apuestas: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    public Map<String, Long> getCountBetsByStatus(String token, Long userId) {
        String url = apiBaseUrl + "/api/bet/by-user/status/" + userId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.trim());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map<String, Long>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<Map<String, Long>>() {}
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Collections.emptyMap();
            } else {
                throw new RuntimeException("Error al obtener apuestas: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Collections.emptyMap();
            }
            throw new RuntimeException("Error al obtener apuestas: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    public Map<String, Long> getCountBetsByDate(String token, Long userId) {
        String url = apiBaseUrl + "/api/bet/by-user/date/" + userId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.trim());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map<String, Long>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<Map<String, Long>>() {}
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Collections.emptyMap();
            } else {
                throw new RuntimeException("Error al obtener apuestas: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Collections.emptyMap();
            }
            throw new RuntimeException("Error al obtener apuestas: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    public Long getTotalBetsByEvent(String token, Long eventId) {
        String url = apiBaseUrl + "/api/bet/event/" + eventId + "/totalBets";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.trim());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Long> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Long.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                return 0L;
            } else {
                throw new RuntimeException("Error al obtener apuestas: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NO_CONTENT) {
                return 0L;
            }
            throw new RuntimeException("Error al obtener apuestas: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    public List<UserBetStats> getUserRankingByCorrectBets(String token, Long eventId) {
        String url = apiBaseUrl + "/api/bet/event/" + eventId + "/user-ranking";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.trim());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<List<UserBetStats>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<UserBetStats>>() {}
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                return Collections.emptyList();
            } else {
                throw new RuntimeException("Error al obtener el ranking: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NO_CONTENT) {
                return Collections.emptyList();
            }
            throw new RuntimeException("Error al obtener el ranking: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    public Market getMostPopularMarketByEvent(String token, Long eventId) {
        String url = apiBaseUrl + "/api/bet/event/" + eventId + "/most-popular-market";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.trim());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Market> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<Market>() {}
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                return null;
            } else {
                throw new RuntimeException("Error al obtener el mercado más popular: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NO_CONTENT) {
                return null;
            }
            throw new RuntimeException("Error al obtener el mercado más popular: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    public Option getMostPopularOptionByEvent(String token, Long eventId) {
        String url = apiBaseUrl + "/api/bet/event/" + eventId + "/most-popular-option";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.trim());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Option> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<Option>() {}
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                return null;
            } else {
                throw new RuntimeException("Error al obtener la opción más popular: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NO_CONTENT) {
                return null;
            }
            throw new RuntimeException("Error al obtener la opción más popular: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    public Map<String, Map<String, Long>> getBetDistributionByMarket(String token, Long eventId) {
        String url = apiBaseUrl + "/api/bet/event/" + eventId + "/bet-distribution";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.trim());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map<String, Map<String, Long>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<Map<String, Map<String, Long>>>() {}
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                return Collections.emptyMap();
            } else {
                throw new RuntimeException("Error al obtener distribución de apuestas: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NO_CONTENT) {
                return Collections.emptyMap();
            }
            throw new RuntimeException("Error al obtener distribución de apuestas: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    public void saveBet(String token, Bet bet) {
        String url = apiBaseUrl + "/api/bet";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.trim());
        headers.setContentType(MediaType.APPLICATION_JSON);

        StringBuilder jsonBody = new StringBuilder("{");

        jsonBody.append("\"userId\": \"").append(bet.getUser().getId()).append("\",");

        jsonBody.append("\"optionId\": ").append(bet.getOption().getId());

        jsonBody.append("}");

        System.out.println("JSON enviado:\n" + jsonBody);

        HttpEntity<String> entity = new HttpEntity<>(jsonBody.toString(), headers);

        try {
            ResponseEntity<Bet> response = restTemplate.exchange(url, HttpMethod.POST, entity, Bet.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Error al crear la votacion: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error al crear la votacion: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    public void deleteBetById(String token, Long betId) {
        String url = apiBaseUrl + "/api/bet/" + betId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.trim());

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
            if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                // Éxito, no hay cuerpo esperado
            } else {
                throw new RuntimeException("Error al eliminar la votación: " + response.getStatusCode() + " - " + response.getBody());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error al eliminar la votación: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    //METHODS AUX
    private Bet fromDTO(ResponseBetDTO dto) {
        if (dto == null) return null;

        Bet bet = new Bet();
        bet.setId(dto.getId());
        bet.setBetDate(dto.getBetDate());
        bet.setStatus(dto.getStatus());

        // Convertir option
        if (dto.getOption() != null) {
            SumaryOptionDTO optionDTO = dto.getOption();
            Option option = new Option();
            option.setId(optionDTO.getId());
            option.setText(optionDTO.getText());
            option.setPercentage(optionDTO.getPercentage());

            // Convertir market
            if (optionDTO.getMarket() != null) {
                ResponseMarketDTO marketDTO = optionDTO.getMarket();
                Market market = new Market();
                market.setId(marketDTO.getId());
                market.setQuestion(marketDTO.getQuestion());

                // Convertir event
                if (marketDTO.getEvent() != null) {
                    SumaryEventDTO eventDTO = marketDTO.getEvent();
                    Event event = new Event();
                    event.setId(eventDTO.getId());
                    event.setTitle(eventDTO.getTitle());
                    market.setEvent(event);
                }

                option.setMarket(market);
            }

            bet.setOption(option);
        }

        return bet;
    }
}
