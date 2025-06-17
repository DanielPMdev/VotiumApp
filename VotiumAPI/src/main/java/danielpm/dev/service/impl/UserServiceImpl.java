package danielpm.dev.service.impl;

import danielpm.dev.entity.Role;
import danielpm.dev.entity.User;
import danielpm.dev.repository.UserRepository;
import danielpm.dev.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author danielpm.dev@gmail.com
 */
@Service
public class UserServiceImpl implements UserService {

    @Value("${user.default.image}")
    private String defaultImage;

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserServiceImpl(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    @Override
    public Page<User> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByName(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Override
    public User createOrUpdateUser(User user) {
        if (user.getId() == null) {
            // Creación
            User userToSave = new User(
                    user.getUsername(),
                    passwordEncoder.encode(user.getPassword()),
                    user.getEmail(),
                    user.getBetList(),
                    user.getEventList(),
                    user.getUrlImage() != null ? user.getUrlImage() : defaultImage,
                    user.getRole()
            );
            return userRepository.save(userToSave);
        } else {
            // Actualización
            Optional<User> existingUserOpt = userRepository.findById(user.getId());
            if (existingUserOpt.isPresent()) {
                User existingUser = existingUserOpt.get();

                String newPassword = user.getPassword();

                if (newPassword != null && !newPassword.isBlank()) {
                    // Verificamos si el password que recibimos es ya un hash (probablemente empieza por $2a$ o similar)
                    if (!newPassword.startsWith("$2a$") && !newPassword.startsWith("$2b$") && !newPassword.startsWith("$2y$")) {
                        // Es texto plano → lo codificamos
                        existingUser.setPassword(passwordEncoder.encode(newPassword));
                    } else {
                        // Ya viene codificado (posiblemente fue reenviado desde el frontend sin cambiar)
                        existingUser.setPassword(newPassword);
                    }
                }

                // Actualiza solo los campos necesarios
                existingUser.setUsername(user.getUsername());
                existingUser.setEmail(user.getEmail());
                existingUser.setRole(user.getRole());
                existingUser.setUrlImage(user.getUrlImage());
                existingUser.setEventList(user.getEventList());
                existingUser.setBetList(user.getBetList());

                return userRepository.save(existingUser);
            } else {
                throw new RuntimeException("User con ID " + user.getId() + " no encontrado");
            }
        }
    }


    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void deleteAllUsers() {
        userRepository.deleteAll();
    }
}
