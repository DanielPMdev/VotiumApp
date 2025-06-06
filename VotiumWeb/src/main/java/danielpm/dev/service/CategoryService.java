package danielpm.dev.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import danielpm.dev.model.Category;
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
public class CategoryService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${api.base-url}")
    private String apiBaseUrl;

    public CategoryService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public List<Category> getAllCategories(String token) {
        String url = apiBaseUrl + "/api/categories";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.trim());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<List<Category>> response = restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<Category>>() {});
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                throw new RuntimeException("Error al obtener las categorias " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error al obtener las categorias: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    public Category getCategoryById(String token, Long id) {
        String url = apiBaseUrl + "/api/category/" + id;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.trim());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Category> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Category.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new RuntimeException("Error al obtener la categoría: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error al obtener la categoría: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    public void saveCategory(String token, Category category) {
        String url = apiBaseUrl + "/api/category";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.trim());
        headers.setContentType(MediaType.APPLICATION_JSON);

        StringBuilder jsonBody = new StringBuilder("{");

        jsonBody.append("\"name\": \"").append(category.getName()).append("\"}");

        System.out.println("JSON enviado:\n" + jsonBody);

        HttpEntity<String> entity = new HttpEntity<>(jsonBody.toString(), headers);

        try {
            ResponseEntity<Category> response = restTemplate.exchange(url, HttpMethod.POST, entity, Category.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Error al crear la categoría: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error al crear la categoría: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    public void updateCategory(String token, Category category) {
        String url = apiBaseUrl + "/api/category/" + category.getId();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.trim());
        headers.setContentType(MediaType.APPLICATION_JSON);

        StringBuilder jsonBody = new StringBuilder("{");

        jsonBody.append("\"id\": ").append(category.getId()).append(",");
        jsonBody.append("\"name\": \"").append(category.getName()).append("\"");

        jsonBody.append("}");

        System.out.println("JSON enviado (update):\n" + jsonBody);

        HttpEntity<String> entity = new HttpEntity<>(jsonBody.toString(), headers);

        try {
            ResponseEntity<Category> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Category.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Error al actualizar la categoría: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error al actualizar la categoría: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    public void deleteCategoryById(String token, Long categoryId) {
        String url = apiBaseUrl + "/api/category/" + categoryId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.trim());

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
            if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                // Éxito, no hay cuerpo esperado
            } else {
                throw new RuntimeException("Error al eliminar la categoría: " + response.getStatusCode() + " - " + response.getBody());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error al eliminar la categoría: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }
}
