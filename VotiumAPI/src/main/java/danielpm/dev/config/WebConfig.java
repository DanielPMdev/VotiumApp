package danielpm.dev.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author danielpm.dev
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Aplica a todos los endpoints
                .allowedOrigins("http://localhost:3000") // Orígenes permitidos ("https://frontend.tudominio.com")
                .allowedMethods("GET", "POST", "PUT", "DELETE") // Métodos permitidos
                .allowedHeaders("*") // Todos los encabezados o lista específica
                .allowCredentials(true); // Si necesitas enviar cookies o autenticación
                //.maxAge(3600); // Tiempo de caché de las preflight requests (optimiza el rendimiento al evitar solicitudes repetidas.)
    }
}