package danielpm.dev.controller;

import danielpm.dev.service.AuthService;
import danielpm.dev.service.PermissionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author danielpm.dev
 */
@Controller
public class AuthController {

    private final AuthService authService;
    private final PermissionService permissionService;

    public AuthController(AuthService authService, PermissionService permissionService) {
        this.authService = authService;
        this.permissionService = permissionService;
    }

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        String token = (String) session.getAttribute("jwtToken");
        Boolean isAuthenticated = token != null && !token.isEmpty();

        boolean isAdmin = false;

        if (isAuthenticated) {
            isAdmin = permissionService.isAdmin(token);
        }

        model.addAttribute("admin", isAdmin);
        model.addAttribute("authenticated", isAuthenticated);

        return "index";
    }

    @GetMapping("/register")
    public String registerForm(Model model, HttpSession session) {
        String token = (String) session.getAttribute("jwtToken");
        Boolean isAuthenticated = token != null && !token.isEmpty();
        model.addAttribute("authenticated", isAuthenticated);
        return "auth/register"; // Muestra el formulario de registro
    }

    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password, @RequestParam String email, Model model, HttpSession session) {
        try {
            // Aquí llamas al servicio para registrar al usuario
            authService.register(username, password, email);
            // Si el registro es exitoso, podrías iniciar sesión automáticamente o redirigir a login
            String token = authService.authenticate(username, password); // Autenticar tras registro
            session.setAttribute("jwtToken", token);
            return "redirect:/"; // Redirige a la página principal
        } catch (Exception e) {
            model.addAttribute("error", "Error al registrarse: " + e.getMessage());
            model.addAttribute("authenticated", false);
            return "auth/register"; // Vuelve al formulario con el mensaje de error
        }
    }

    @GetMapping("/login")
    public String loginForm(Model model, HttpSession session) {
        String token = (String) session.getAttribute("jwtToken");
        Boolean isAuthenticated = token != null && !token.isEmpty();
        model.addAttribute("authenticated", isAuthenticated);
        return "auth/login"; // Devuelve la plantilla login.html
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        try {
            String token = authService.authenticate(username, password);
            session.setAttribute("jwtToken", token);
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("error", "Credenciales inválidas");
            model.addAttribute("authenticated", false);
            return "auth/login";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session, HttpServletRequest request) {
        String token = (String) session.getAttribute("jwtToken");
        if (token != null && !token.isEmpty()) {
            try {
                authService.logout(token); // Llama al servicio para invalidar el token en la API
                session.invalidate(); // Invalida la sesión local
                return "redirect:/login?logout";
            } catch (Exception e) {
                return "redirect:/perfil?error=Error al cerrar sesión: " + e.getMessage();
            }
        }
        // Si no hay token, simplemente invalida la sesión y redirige
        session.invalidate();
        return "redirect:/login";
    }

    //CAMBIOS DE CONTRASEÑA

    @GetMapping("/changePassword")
    public String showForgotPasswordForm(Model model) {
        model.addAttribute("authenticated", false);
        return "auth/change-password";
    }

    @PostMapping("/changePassword")
    public String requestPasswordReset(@RequestParam String username, Model model) {
        try {
            model.addAttribute("authenticated", false);
            Long userId = authService.getUserIdByUsername(username); // Obtener el ID del usuario
            model.addAttribute("userId", userId);
            return "auth/change-password-form";
        } catch (Exception e) {
            model.addAttribute("error", "Usuario no encontrado o error: " + e.getMessage());
            return "auth/change-password";
        }
    }

    @PostMapping("/changePassword/reset")
    public String resetPassword(@RequestParam Long userId, @RequestParam String newPassword, @RequestParam String confirmPassword, Model model) {
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Las contraseñas no coinciden");
            model.addAttribute("userId", userId);
            return "auth/change-password-form";
        }
        try {
            authService.resetPassword(userId, newPassword);
            model.addAttribute("success", "Contraseña actualizada correctamente. Inicia sesión con tu nueva contraseña.");
            return "auth/login";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cambiar la contraseña: " + e.getMessage());
            model.addAttribute("userId", userId);
            return "auth/change-password-form";
        }
    }
}