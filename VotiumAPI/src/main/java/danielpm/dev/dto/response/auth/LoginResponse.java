package danielpm.dev.dto.response.auth;


import danielpm.dev.entity.Role;

/**
 * @author danielpm.dev
 */
public record LoginResponse(String username, Role role, String token) {
}
