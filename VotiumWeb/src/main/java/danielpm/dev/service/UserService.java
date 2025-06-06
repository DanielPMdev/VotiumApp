package danielpm.dev.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import danielpm.dev.model.Category;
import danielpm.dev.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author danielpm.dev
 */
@Service
public class UserService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${api.base-url}")
    private String apiBaseUrl;

    @Value("${jwt.secret.file}")
    private String secretFilePath;

    private String SECRET_KEY;
    private static SecretKey signingKey;

    @PostConstruct
    public void init() {
        try {
            SECRET_KEY = new String(Files.readAllBytes(Paths.get(secretFilePath))).trim();
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo encontrar el archivo en la ruta: " + secretFilePath, e);
        }
        this.signingKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    public UserService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public User getAuthenticatedUser(String token) {
        Long userId = getUserIdFromToken(token);

        String url = apiBaseUrl + "/api/user/" + userId;

        HttpHeaders headers = new HttpHeaders();
        String authHeader = "Bearer " + token.trim(); // Aseguro que no haya espacios extra
        headers.set("Authorization", authHeader);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<User> response = restTemplate.exchange(url, HttpMethod.GET, entity, User.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                throw new RuntimeException("Error al obtener los datos del usuario: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error al obtener los datos del usuario: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    public static Long getUserIdFromToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token);
            Claims claims = claimsJws.getPayload();
            String subject = claims.get(Claims.SUBJECT, String.class);
            return Long.parseLong(subject);
        } catch (NumberFormatException e) {
            throw new RuntimeException("El ID en el token no es un número válido: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error al extraer el ID del token: " + e.getMessage());
        }
    }

    public static String getUserRoleFromToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token);
            Claims claims = claimsJws.getPayload();
            return claims.get("role", String.class);
        } catch (Exception e) {
            throw new RuntimeException("Error al extraer el rol del token: " + e.getMessage());
        }
    }

    public static String getUsernameFromToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token);
            Claims claims = claimsJws.getPayload();
            return claims.get("username", String.class);
        } catch (Exception e) {
            throw new RuntimeException("Error al extraer el username del token: " + e.getMessage());
        }
    }

    public List<User> getAllUsers(String token) {
        String url = apiBaseUrl + "/api/users";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.trim());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<List<User>> response = restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<User>>() {});
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                throw new RuntimeException("Error al obtener los usuarios " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error al obtener los usuarios: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    public User getUserById(String token, Long id) {
        String url = apiBaseUrl + "/api/user/" + id;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.trim());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<User> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    User.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new RuntimeException("Error al obtener el usuario: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error al obtener el usuario: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    // Actualizar el email mediante llamada a la API
    public void updateEmail(String token, String email) {
        Long userId = getUserIdFromToken(token);
        String url = apiBaseUrl + "/api/user/" + userId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.trim());
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Crear el cuerpo de la solicitud con el nuevo email
        String requestBody = "{\"email\": \"" + email + "\"}";
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<User> response = restTemplate.exchange(url, HttpMethod.PUT, entity, User.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Error al actualizar el email: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error al actualizar el email: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    // Actualizar la imagen mediante llamada a la API
    public void updateImage(String token, String imageUrl) {
        Long userId = getUserIdFromToken(token);
        String url = apiBaseUrl + "/api/user/image/" + userId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.trim());
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Crear el cuerpo de la solicitud con la nueva URL de la imagen
        String requestBody = "{\"imageUrl\": \"" + imageUrl + "\"}";
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<User> response = restTemplate.exchange(url, HttpMethod.PUT, entity, User.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Error al actualizar la imagen: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error al actualizar la imagen: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    // Actualizar la contraseña mediante llamada a la API
    public void updatePassword(String token, String password) {
        Long userId = getUserIdFromToken(token);
        String url = apiBaseUrl + "/api/user/changePassword/" + userId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.trim());
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Crear el cuerpo de la solicitud con la nueva URL de la imagen
        String requestBody = "{\"password\": \"" + password + "\"}";
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<User> response = restTemplate.exchange(url, HttpMethod.PUT, entity, User.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Error al actualizar la contraseña: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error al actualizar la contraseña: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    public void updateRoleUser(String token, User user) {
        String url = apiBaseUrl + "/api/user/" + user.getId();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.trim());
        headers.setContentType(MediaType.APPLICATION_JSON);

        StringBuilder jsonBody = new StringBuilder("{");

        jsonBody.append("\"id\": ").append(user.getId()).append(",");
        jsonBody.append("\"email\": \"").append(user.getEmail()).append("\",");
        jsonBody.append("\"role\": \"").append(user.getRole()).append("\"");

        jsonBody.append("}");

        System.out.println("JSON enviado (update):\n" + jsonBody);

        HttpEntity<String> entity = new HttpEntity<>(jsonBody.toString(), headers);

        try {
            ResponseEntity<User> response = restTemplate.exchange(url, HttpMethod.PUT, entity, User.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Error al actualizar el usuario: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error al actualizar el usuario: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    // Eliminar el User mediante llamada a la API
    public void deleteUserById(String token, Long userId) {
        String url = apiBaseUrl + "/api/user/" + userId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.trim());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
            if (response.getStatusCode() != HttpStatus.NO_CONTENT) {
                throw new RuntimeException("Error al eliminar el usuario: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error al eliminar el usuario: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }
}