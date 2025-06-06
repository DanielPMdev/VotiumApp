package danielpm.dev.controller;

/**
 * @author danielpm.dev
 */

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;
import java.util.List;

@Controller
public class PortfolioController {

    @GetMapping("/portfolio")
    public String portfolio(Model model, HttpSession session) {
        String token = (String) session.getAttribute("jwtToken");
        Boolean isAuthenticated = token != null && !token.isEmpty();

        model.addAttribute("authenticated", isAuthenticated);

        // Datos personales
        model.addAttribute("urlImg", "/img/DanielPM_Foto.jpg");
        model.addAttribute("acerca", "Soy un desarrollador apasionado por crear soluciones tecnológicas innovadoras. " +
                "Aprendiendo, creando y creciendo en el mundo del Desarrollo de Backend con Java.");
        model.addAttribute("cvUrl", "/files/Daniel_Porras_CV_2025.pdf");
        model.addAttribute("linkedinUrl", "https://www.linkedin.com/in/danielpmdev/");
        model.addAttribute("githubUrl", "https://github.com/DanielPMdev");
        model.addAttribute("email", "danielpm.dev@gmail.com");

        // Experiencia
        List<Experiencia> experiencias = List.of(
                new Experiencia(
                        "Desarrollador de Software",
                        "Minsait",
                        "Marzo 2025 - Presente",
                        """
                                ● Desarrollé una aplicación empresarial en Appian usando Record Types para modelar datos.\s
                                ● Configuré filtros, vistas y acciones para mejorar la navegación.\s
                                ● Modifiqué interfaces con variables locales y ajusté Process Models a nuevos flujos.\s
                                ● Creé un Site centralizando las interfaces y gestioné la visibilidad de componentes según grupos y características.\s
                                ● Optimicé consultas para la recuperación eficiente de datos.
                               \s"""
                )
        );
        model.addAttribute("experiencias", experiencias);

        // Habilidades
        List<Habilidad> habilidades = List.of(
                new Habilidad("Java", "Avanzado"),
                new Habilidad("Spring Boot", "Avanzado"),
                new Habilidad("Thymeleaf", "Intermedio"),
                new Habilidad("Bootstrap", "Intermedio"),
                new Habilidad("Kotlin", "Intermedio")
        );
        model.addAttribute("habilidades", habilidades);

        // Proyectos - AÑADIR IMAGENES O ALGO A LOS PROYECTOS
        List<Proyecto> proyectos = List.of(
                new Proyecto(
                        "Votium",
                        "Plataforma de votaciones segura y eficiente construida con Spring Boot y Thymeleaf.",
                        "https://github.com/DanielPMdev/VotiumApp",
                        "https://www.shutterstock.com/image-vector/image-coming-soon-no-picture-600nw-2450891047.jpg",
                        List.of("Spring Boot", "Spring Security", "JWT", "Thymeleaf", "Bootstrap", "Java")
                ),
                new Proyecto(
                        "PetTracker",
                        "API REST en Spring Boot para gestionar perfiles de mascotas, historial médico y actividad. Incluye autenticación con Spring Security y JWT, endpoints optimizados y una interfaz web con Thymeleaf y Bootstrap.",
                        "https://github.com/DanielPMdev/PetTracker",
                        "https://www.shutterstock.com/image-vector/image-coming-soon-no-picture-600nw-2450891047.jpg",
                        List.of("Spring Boot", "Spring Security", "JWT", "Thymeleaf", "Bootstrap", "Java")
                )
        );
        model.addAttribute("proyectos", proyectos);

        return "aboutme";
    }

    // Clases internas para modelar los datos
    public static class Experiencia {
        public String titulo, empresa, periodo, descripcion;

        public Experiencia(String titulo, String empresa, String periodo, String descripcion) {
            this.titulo = titulo;
            this.empresa = empresa;
            this.periodo = periodo;
            this.descripcion = descripcion;
        }
    }

    public static class Habilidad {
        public String nombre, nivel;

        public Habilidad(String nombre, String nivel) {
            this.nombre = nombre;
            this.nivel = nivel;
        }
    }

    public static class Proyecto {
        public String imageUrl, nombre, descripcion, url;
        public List<String> tecnologias;

        public Proyecto(String nombre, String descripcion, String url, String imageUrl, List<String> tecnologias) {
            this.imageUrl = imageUrl;
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.url = url;
            this.tecnologias = tecnologias;
        }
    }
}