package danielpm.dev.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

/**
 * @author danielpm.dev
 */
@Controller
public class CustomErrorController implements ErrorController {

    private static final Logger logger = LoggerFactory.getLogger(CustomErrorController.class);

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model, HttpSession session) {
        String token = (String) session.getAttribute("jwtToken");
        Boolean isAuthenticated = token != null && !token.isEmpty();
        model.addAttribute("authenticated", isAuthenticated);

        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);

        int statusCode = status != null ? Integer.parseInt(status.toString()) : 500;

        logger.error("Error con código de estado: {}", statusCode);
        logger.error("Mensaje de error: {}", message);
        if (exception != null) {
            logger.error("Excepción: {}", exception);
        }

        model.addAttribute("status", statusCode);
        model.addAttribute("message", message != null ? message : "Sin mensaje");
        model.addAttribute("exception", exception);

        // Redirigir a plantillas personalizadas para ciertos errores comunes
        switch (statusCode) {
            case 404:
                return "error/404";
            case 500:
                return "error/500";
            case 403:
                return "error/403";
            default:
                model.addAttribute("error", "Error desconocido");
        }
        return "error/error";
    }

    @GetMapping("/provocar500")
    public String provocar500() {
        throw new RuntimeException("Esto es un error 500 de prueba");
    }

    @GetMapping("/provocar403")
    public String provocar403(HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso prohibido de prueba");
        return null;
    }

    @GetMapping("/provocarDefault")
    public String provocarDefault(HttpServletResponse response) throws IOException {
        response.sendError(418, "Soy una tetera ☕️");
        return null;
    }

}



