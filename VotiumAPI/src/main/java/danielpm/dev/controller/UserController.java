package danielpm.dev.controller;

import danielpm.dev.dto.request.image.ImageUpdateRequest;
import danielpm.dev.dto.request.user.ChangeUserDTO;
import danielpm.dev.dto.request.user.FullUserDTO;
import danielpm.dev.dto.request.user.PasswordUserDTO;
import danielpm.dev.dto.response.user.PaginatedResponseUserDTO;
import danielpm.dev.entity.*;
import danielpm.dev.service.PasswordResetTokenService;
import danielpm.dev.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author danielpm.dev
 */
@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final PasswordResetTokenService passwordResetTokenService;

    public UserController(UserService userService, PasswordResetTokenService passwordResetTokenService) {
        this.userService = userService;
        this.passwordResetTokenService = passwordResetTokenService;
    }

    /*
        GET http://localhost:8080/api/users
     */
    @GetMapping("/users")
    public ResponseEntity<PaginatedResponseUserDTO> findAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        Page<User> userPage = userService.getAllUsers(page, size);

        return ResponseEntity.ok(new PaginatedResponseUserDTO(userPage));
    }


    /*
        GET http://localhost:8080/api/user/7
     */
    @GetMapping("/user/{id}")
    public ResponseEntity<User> findAllById(@PathVariable Long id) {
        if (id < 0) {
            return ResponseEntity.badRequest().build();
        }

        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /*
        GET http://localhost:8080/api/user/username/paco
     */
    @GetMapping("/user/username/{username}")
    public ResponseEntity<User> findUserByUsername(@PathVariable String username) {
        if (username.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        return userService.getUserByName(username)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /*
       GET http://localhost:8080/api/user/7/events
    */
    @GetMapping("/user/{id}/events")
    public ResponseEntity<List<Event>> findAllEvents(@PathVariable Long id) {
        if (id < 0) {
            return ResponseEntity.badRequest().build();
        }

        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(user.getEventList()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /*
       GET http://localhost:8080/api/user/7/bets
    */
    @GetMapping("/user/{id}/bets")
    public ResponseEntity<List<Bet>> findAllBets(@PathVariable Long id) {
        if (id < 0) {
            return ResponseEntity.badRequest().build();
        }

        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(user.getBetList()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /*
        POST http://localhost:8080/api/user
     */
    @PostMapping("/user")
    public ResponseEntity<User> createUser(@RequestBody FullUserDTO userDTO) {
        if (userDTO.getId() != null) //Si ya tiene un id asignado
            return ResponseEntity.badRequest().build();

        User userCreated = new User();
        userCreated.setUsername(userDTO.getUsername());
        userCreated.setPassword(userDTO.getPassword());
        userCreated.setEmail(userDTO.getEmail());
        userCreated.setRole(Role.USER);

        userService.createOrUpdateUser(userCreated);
        return ResponseEntity.ok(userCreated);
    }

    /*
        PUT http://localhost:8080/api/user/3
     */
    @PutMapping("/user/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody ChangeUserDTO userDTO) {
        if (!userService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        // Obtener el User existente desde el servicio
        User userToUpdate = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userToUpdate.setEmail(userDTO.getEmail());
        userToUpdate.setRole(userDTO.getRole());

        User updatedUser = userService.createOrUpdateUser(userToUpdate);
        return ResponseEntity.ok(updatedUser);
    }

    /*
        PUT http://localhost:8080/api/user/changePassword/3
     */
    @PutMapping("/user/changePassword/{id}")
    public ResponseEntity<User> updatePasswordUser(@PathVariable Long id, @RequestBody PasswordUserDTO userDTO) {
        if (!userService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        // Obtener el User existente desde el servicio
        User userToUpdate = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userToUpdate.setPassword(userDTO.getPassword());

        User updatedUser = userService.createOrUpdateUser(userToUpdate);
        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/user/reset-password-with-token")
    public ResponseEntity<String> resetPasswordWithToken(@RequestBody Map<String, String> payload) {
        String token = payload.get("token");
        String newPassword = payload.get("password");

        PasswordResetToken resetToken = passwordResetTokenService.getPasswordResetByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token expirado");
        }

        User user = resetToken.getUser();
        user.setPassword(newPassword);
        userService.createOrUpdateUser(user);

        passwordResetTokenService.deletePasswordResetTokenById(resetToken.getId());

        return ResponseEntity.ok("Contraseña actualizada correctamente");
    }

    /*
        PUT http://localhost:8080/api/user/image/3
     */
    @PutMapping("/user/image/{id}")
    public ResponseEntity<User> updateImageUser(@PathVariable Long id, @RequestBody ImageUpdateRequest imgRequest) {
        if (!userService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        //Obtener el User existente mediante el ID
        User userToUpdate = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (imgRequest.getImageUrl() != null && !imgRequest.getImageUrl().isEmpty()) {
            userToUpdate.setUrlImage(imgRequest.getImageUrl());

            User updatedUser = userService.createOrUpdateUser(userToUpdate);
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    /*
        DELETE http://localhost:8080/api/user/{identifier}
     */
    @DeleteMapping("/user/{identifier}")
    public ResponseEntity<Void> deleteUserById(@PathVariable("identifier") Long id) {
        if (!userService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
