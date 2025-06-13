package danielpm.dev.service.impl;

import danielpm.dev.entity.Bet;
import danielpm.dev.entity.PasswordResetToken;
import danielpm.dev.entity.User;
import danielpm.dev.repository.PasswordResetTokenRepository;
import danielpm.dev.service.PasswordResetTokenService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author danielpm.dev
 */
@Service
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public PasswordResetTokenServiceImpl(PasswordResetTokenRepository passwordResetTokenRepository) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    @Override
    public boolean existsById(Long id) {
        return passwordResetTokenRepository.existsById(id);
    }

    @Override
    public List<PasswordResetToken> getAllPasswordResetTokens() {
        return passwordResetTokenRepository.findAll();
    }

    @Override
    public Optional<PasswordResetToken> getPasswordResetById(Long id) {
        return passwordResetTokenRepository.findById(id);
    }

    @Override
    public Optional<PasswordResetToken> getPasswordResetByToken(String token) {
        return passwordResetTokenRepository.findByToken(token);
    }

    @Override
    public Optional<PasswordResetToken> getPasswordResetByUserId(Long userId) {
        return passwordResetTokenRepository.findByUser_Id(userId);
    }

    @Override
    public PasswordResetToken createOrUpdatePasswordResetToken(PasswordResetToken tokenData) {
        if (tokenData.getId() == null) {
            // Crear nuevo token
            if (tokenData.getUser() == null || tokenData.getUser().getId() == null) {
                throw new IllegalArgumentException("El usuario asociado no puede ser nulo");
            }

            return passwordResetTokenRepository.save(tokenData);
        } else {
            // Actualizar token existente
            Optional<PasswordResetToken> existingTokenOpt = passwordResetTokenRepository.findById(tokenData.getId());

            if (existingTokenOpt.isPresent()) {
                PasswordResetToken existingToken = existingTokenOpt.get();

                existingToken.setToken(tokenData.getToken());
                existingToken.setExpiryDate(tokenData.getExpiryDate());

                // Si se desea cambiar el usuario asociado
                if (tokenData.getUser() != null && tokenData.getUser().getId() != null) {
                    existingToken.setUser(tokenData.getUser());
                }

                return passwordResetTokenRepository.save(existingToken);
            } else {
                throw new RuntimeException("Token con ID " + tokenData.getId() + " no encontrado");
            }
        }
    }


    @Override
    public void deletePasswordResetTokenById(Long id) {
        passwordResetTokenRepository.deleteById(id);
    }

    @Override
    public void deleteAllPasswordResetToken() {
        passwordResetTokenRepository.deleteAll();
    }
}
