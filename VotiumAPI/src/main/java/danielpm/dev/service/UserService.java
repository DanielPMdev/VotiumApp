package danielpm.dev.service;

import danielpm.dev.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

/**
 * @author danielpm.dev@gmail.com
 */
public interface UserService {
    boolean existsById(Long id);

    //Methods retrive
    Page<User> getAllUsers(int page, int size);

    Optional<User> getUserById(Long id);
    Optional<User> getUserByEmail(String email);
    Optional<User> getUserByName(String username);

    //Optional<List<Pet>> getPetsByUserId(Long id);

    //Methods create / update
    User createOrUpdateUser(User User);

    //Methods delete
    void deleteUserById(Long id);
    void deleteAllUsers();
}
