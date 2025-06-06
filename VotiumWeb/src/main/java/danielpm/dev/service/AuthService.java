package danielpm.dev.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import danielpm.dev.dto.request.auth.LoginRequest;
import danielpm.dev.dto.request.auth.RegisterRequest;
import danielpm.dev.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * @author danielpm.dev
 */
@Service
public class AuthService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${api.base-url}")
    private String apiBaseUrl;

    public AuthService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper; // Inyectamos ObjectMapper para parsear JSON
    }

    public void register(String username, String password, String email) {
        String url = apiBaseUrl + "/auth/register"; // Endpoint de registro

        // Crear el cuerpo de la solicitud
        RegisterRequest registerRequest = new RegisterRequest(username, password, email);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RegisterRequest> entity = new HttpEntity<>(registerRequest, headers);

        try {
            // Hacer la solicitud POST al endpoint de registro
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            if (response.getStatusCode() != HttpStatus.CREATED && response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Error al registrar: " + response.getStatusCode());
            }
            // No necesitamos extraer nada si la API no devuelve un token aquí
            // El éxito se confirma con el código de estado (201 Created o 200 OK)
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error al registrar: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    // Autenticación para obtener el JWT
    public String authenticate(String username, String password) {
        String url = apiBaseUrl + "/auth/login"; // Ajusta el endpoint según tu API

        // Crear el cuerpo de la solicitud como un objeto DTO
        LoginRequest loginRequest = new LoginRequest(username, password);

        // Configurar encabezados
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Crear la entidad HTTP con el cuerpo y los encabezados
        HttpEntity<LoginRequest> entity = new HttpEntity<>(loginRequest, headers);

        try {
            // Hacer la solicitud POST a la API
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            // Verificar que la respuesta sea exitosa
            if (response.getStatusCode() == HttpStatus.OK) {
                return extractToken(response.getBody());
            } else {
                throw new RuntimeException("Error en la autenticación: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error en la autenticación: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    // Method para cerrar sesión
    public void logout(String token) {
        String url = apiBaseUrl + "/auth/logout";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.trim());

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Error al cerrar sesión en la API: " + response.getStatusCode() + " - " + response.getBody());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error al cerrar sesión en la API: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }


    //CAMBIAR CONTRASEÑA
    public Long getUserIdByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario no puede ser nulo o vacío");
        }

        String url = apiBaseUrl + "/api/user/username/" + username;
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<User> response = restTemplate.exchange(url, HttpMethod.GET, entity, User.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody().getId();
            } else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new RuntimeException("Usuario no encontrado para el nombre: " + username);
            } else {
                throw new RuntimeException("Error al buscar el usuario: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new RuntimeException("Usuario no encontrado para el nombre: " + username);
            } else if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new RuntimeException("Solicitud inválida: nombre de usuario no válido");
            } else {
                throw new RuntimeException("Error al contactar la API: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            }
        }
    }

    public void resetPassword(Long userId, String newPassword) {
        String url = apiBaseUrl + "/api/user/changePassword/" + userId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Crear el cuerpo JSON correctamente
        String jsonBody = "{\"password\": \"" + newPassword + "\"}";

        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

        try {
            // Cambiar String.class a User.class para coincidir con la respuesta de la API
            ResponseEntity<User> response = restTemplate.exchange(url, HttpMethod.PUT, entity, User.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException(response.getStatusCode() + " on PUT request for \"" + url + "\": " + response.getBody());
            }
            // Opcional: Puedes usar response.getBody() si necesitas el User devuelto
        } catch (HttpClientErrorException e) {
            throw new RuntimeException(e.getStatusCode() + " on PUT request for \"" + url + "\": " + e.getResponseBodyAsString());
        }
    }

    // Method para extraer el token del cuerpo de la respuesta JSON
    private String extractToken(String responseBody) {
        try {
            // Parsear el JSON usando ObjectMapper
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            // Asumimos que el token está en un campo llamado "token" (ajusta según tu API)
            JsonNode tokenNode = jsonNode.get("token");
            if (tokenNode != null && !tokenNode.isNull()) {
                return tokenNode.asText();
            } else {
                throw new RuntimeException("No se encontró el campo 'token' en la respuesta");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al parsear el token de la respuesta: " + e.getMessage());
        }
    }
}