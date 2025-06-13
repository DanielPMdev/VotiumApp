package danielpm.dev.service;

/**
 * @author danielpm.dev
 */
public interface EmailService {

    void sendPasswordResetEmail(String to, String resetUrl);
}
