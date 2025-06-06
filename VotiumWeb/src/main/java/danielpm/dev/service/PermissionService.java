package danielpm.dev.service;

import danielpm.dev.model.Event;
import org.springframework.stereotype.Service;

import static danielpm.dev.service.UserService.getUserRoleFromToken;
import static danielpm.dev.service.UserService.getUsernameFromToken;

/**
 * @author danielpm.dev
 */
@Service
public class PermissionService {

    public boolean hasPermissions(String token, Event event) {
        boolean isAdmin = getUserRoleFromToken(token).equals("ADMIN");
        boolean isOwner = getUsernameFromToken(token).equals(event.getUser().getUsername());
        return isAdmin || isOwner;
    }

    public boolean isAdmin(String token) {
        return getUserRoleFromToken(token).equals("ADMIN");
    }
}

