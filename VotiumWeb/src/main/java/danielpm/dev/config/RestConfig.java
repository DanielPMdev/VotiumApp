package danielpm.dev.config;

import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author danielpm.dev
 */
@Configuration
public class RestConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            // Obtener la sesión actual desde el contexto de la solicitud
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpSession session = attributes.getRequest().getSession(false); // false para no crear una nueva sesión
                if (session != null) {
                    String token = (String) session.getAttribute("jwtToken");
                    if (token != null) {
                        request.getHeaders().add("Authorization", "Bearer " + token);
                    }
                }
            }
            return execution.execute(request, body);
        });
        return restTemplate;
    }
}