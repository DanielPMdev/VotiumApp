package danielpm.dev.security;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * @author danielpm.dev
 */
@Component
public class TokenBlackList {

    private Set<String> blacklistedTokens = new HashSet<>();

    public void blacklistToken(String token) {
        blacklistedTokens.add(token);
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }
}
