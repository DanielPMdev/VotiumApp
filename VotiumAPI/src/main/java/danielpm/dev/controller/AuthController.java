package danielpm.dev.controller;


import danielpm.dev.dto.request.auth.LoginRequest;
import danielpm.dev.dto.request.user.FullUserDTO;
import danielpm.dev.dto.response.auth.LoginResponse;
import danielpm.dev.entity.PasswordResetToken;
import danielpm.dev.entity.Role;
import danielpm.dev.entity.User;
import danielpm.dev.security.JwtTokenProvider;
import danielpm.dev.security.TokenBlackList;
import danielpm.dev.service.EmailService;
import danielpm.dev.service.PasswordResetTokenService;
import danielpm.dev.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * @author danielpm.dev
 */
@RestController()
public class AuthController {

    private final UserService userService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final EmailService emailService;
    private final AuthenticationManager authManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlackList tokenBlackList;

    public AuthController(UserService userService, PasswordResetTokenService passwordResetTokenService, EmailService emailService, AuthenticationManager authManager, JwtTokenProvider jwtTokenProvider, TokenBlackList tokenBlackList) {
        this.userService = userService;
        this.passwordResetTokenService = passwordResetTokenService;
        this.emailService = emailService;
        this.authManager = authManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenBlackList = tokenBlackList;
    }

    /*
        POST http://localhost:8080/auth/register
     */
    @PostMapping("/auth/register")
    public User save(@RequestBody FullUserDTO userDTO) {

        User userCreated = new User();
        userCreated.setId(userDTO.getId());
        userCreated.setUsername(userDTO.getUsername());
        userCreated.setPassword(userDTO.getPassword());
        userCreated.setEmail(userDTO.getEmail());
        userCreated.setRole(Role.USER);

        return userService.createOrUpdateUser(userCreated);
    }


    /*
        POST http://localhost:8080/auth/login
     */
    @PostMapping("/auth/login")
    public LoginResponse login(@RequestBody LoginRequest loginDTO){
        Authentication authDTO = new UsernamePasswordAuthenticationToken(loginDTO.username(), loginDTO.password());

        Authentication authentication = this.authManager.authenticate(authDTO);
        User user = (User) authentication.getPrincipal();

        String token = this.jwtTokenProvider.generateToken(authentication);

        return new LoginResponse(user.getUsername(),
                user.getRole(),
                token);
    }

    /*
        POST http://localhost:8080/auth/logout
     */
    @PostMapping("/auth/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            tokenBlackList.blacklistToken(token);
            return ResponseEntity.ok("Logout exitoso");
        } else {
            return ResponseEntity.badRequest().body("Token no proporcionado o inválido");
        }
    }

    /*
        POST http://localhost:8080/auth/request-reset
     */
    @PostMapping("/auth/request-reset")
    public ResponseEntity<String> requestResetPassword(@RequestParam String email) {
        Optional<User> optionalUser = userService.getUserByEmail(email);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email no encontrado");
        }

        User user = optionalUser.get();
        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusHours(1); // 1 hora de validez

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(expiry);

        passwordResetTokenService.createOrUpdatePasswordResetToken(resetToken);


        String resetUrl = "http://localhost:9000/changePassword/token?token=" + token;

        // Envía el email
        emailService.sendPasswordResetEmail(user.getEmail(), resetUrl);

        return ResponseEntity.ok("Email enviado");
    }
}

