package danielpm.dev.service;

import danielpm.dev.entity.Bet;
import danielpm.dev.entity.PasswordResetToken;
import danielpm.dev.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * @author danielpm.dev
 */
public interface PasswordResetTokenService {

    boolean existsById(Long id);

    //Methods retrive
    List<PasswordResetToken> getAllPasswordResetTokens();

    Optional<PasswordResetToken> getPasswordResetById(Long id);
    Optional<PasswordResetToken> getPasswordResetByToken(String token);
    Optional<PasswordResetToken> getPasswordResetByUserId(Long userId);


    //Methods create / update
    PasswordResetToken createOrUpdatePasswordResetToken(PasswordResetToken passwordResetToken);

    //Methods delete
    void deletePasswordResetTokenById(Long id);
    void deleteAllPasswordResetToken();
}
