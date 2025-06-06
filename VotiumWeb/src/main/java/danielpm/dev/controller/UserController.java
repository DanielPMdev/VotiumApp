package danielpm.dev.controller;

import danielpm.dev.model.User;
import danielpm.dev.service.AuthService;
import danielpm.dev.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static danielpm.dev.service.UserService.getUserIdFromToken;

/**
 * @author danielpm.dev
 */
@Controller
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        String token = (String) session.getAttribute("jwtToken");
        Boolean isAuthenticated = token != null && !token.isEmpty();
        model.addAttribute("authenticated", isAuthenticated);

        if (isAuthenticated) {
            try {
                User user = userService.getAuthenticatedUser(token);
                model.addAttribute("user", user);
                return "user/profile";
            } catch (Exception e) {
                model.addAttribute("error", "Error al cargar el profile: " + e.getMessage());
                return "user/profile";
            }
        } else {
            return "redirect:/login"; // Si no está autenticado, redirige a login
        }
    }

    // Actualizar el email
    @PostMapping("/profile/update")
    public String updateEmail(@RequestParam("email") String email, HttpSession session, RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("jwtToken");
        Boolean isAuthenticated = token != null && !token.isEmpty();
        //model.addAttribute("authenticated", isAuthenticated);

        if (isAuthenticated) {
            try {
                userService.updateEmail(token, email);
                redirectAttributes.addFlashAttribute("success", "Email actualizado correctamente.");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Error al actualizar el email: " + e.getMessage());
            }
        }

        return "redirect:/profile";
    }

    // Actualizar la imagen
    @PostMapping("/profile/update-image")
    public String updateImage(@RequestParam("imageUrl") String imageUrl, HttpSession session, RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("jwtToken");
        Boolean isAuthenticated = token != null && !token.isEmpty();
        //model.addAttribute("authenticated", isAuthenticated);

        if (isAuthenticated) {
            try {
                userService.updateImage(token, imageUrl);
                redirectAttributes.addFlashAttribute("success", "Imagen actualizada correctamente.");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Error al actualizar la imagen: " + e.getMessage());
            }
        }

        return "redirect:/profile";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(@RequestParam Long userId, @RequestParam String newPassword, @RequestParam String confirmPassword, Model model, HttpSession session) {
        String token = (String) session.getAttribute("jwtToken");
        try {
            authService.resetPassword(userId, newPassword); // Pasamos el token
            model.addAttribute("success", "Contraseña actualizada correctamente.");
            User user = userService.getAuthenticatedUser(token);
            model.addAttribute("user", user);
            return "user/profile";
        } catch (Exception e) {
            model.addAttribute("modalError", "Error al cambiar la contraseña: " + e.getMessage());
            User user = userService.getAuthenticatedUser(token);
            model.addAttribute("user", user);
            return "user/profile";
        }
    }

    // Eliminar el profile
    @PostMapping("/profile/delete")
    public String deleteProfile(HttpSession session, RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("jwtToken");
        Boolean isAuthenticated = token != null && !token.isEmpty();
        //model.addAttribute("authenticated", isAuthenticated);

        if (isAuthenticated) {
            try {
                Long userId = getUserIdFromToken(token);
                userService.deleteUserById(token, userId);
                return "redirect:/logout"; // Redirige al logout tras eliminar
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Error al eliminar el profile: " + e.getMessage());
            }
        }

        return "redirect:/profile";
    }
}