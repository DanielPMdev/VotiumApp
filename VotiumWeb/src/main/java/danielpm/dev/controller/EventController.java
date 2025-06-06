package danielpm.dev.controller;

import danielpm.dev.dto.response.bet.UserBetStats;
import danielpm.dev.model.*;
import danielpm.dev.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.yaml.snakeyaml.error.Mark;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static danielpm.dev.service.UserService.*;

/**
 * @author danielpm.dev
 */
@Controller
public class EventController {

    private final EventService eventService;
    private final CategoryService categoryService;
    private final PermissionService permissionService;
    private final UserService userService;
    private final BetService betService;

    public EventController(EventService eventService, CategoryService categoryService, PermissionService permissionService, UserService userService, BetService betService) {
        this.eventService = eventService;
        this.categoryService = categoryService;
        this.permissionService = permissionService;
        this.userService = userService;
        this.betService = betService;
    }

    @GetMapping("/events")
    public String events(Model model, HttpSession session) {
        String token = (String) session.getAttribute("jwtToken");
        Boolean isAuthenticated = token != null && !token.isEmpty();
        model.addAttribute("authenticated", isAuthenticated);

        if (isAuthenticated) {
            try {
                List<Event> eventList = eventService.getAllEvents(token);
                model.addAttribute("events", eventList);
                return "event/events";
            } catch (Exception e) {
                model.addAttribute("error", "Error al cargar los eventos: " + e.getMessage());
                return "event/events";
            }
        } else {
            return "redirect:/login"; // Si no está autenticado, redirige a login
        }
    }

    @GetMapping("/event/{id}")
    public String eventDetails(@PathVariable Long id, @RequestParam(required = false) Boolean editing, Model model, HttpSession session) {
        String token = (String) session.getAttribute("jwtToken");
        Boolean isAuthenticated = token != null && !token.isEmpty();
        model.addAttribute("authenticated", isAuthenticated);

        if (isAuthenticated) {
            try {
                Event event = eventService.getEventById(token, id);

                boolean hasPermissions = permissionService.hasPermissions(token, event);

                model.addAttribute("isEventClosed", event.isClosedEvent());
                model.addAttribute("permisions", hasPermissions);
                model.addAttribute("event", event);
                model.addAttribute("categories", categoryService.getAllCategories(token));
                model.addAttribute("editing", editing != null && editing);
                return "event/event-details";
            } catch (RuntimeException e) {
                model.addAttribute("error", "Error al cargar los detalles de la evento: " + e.getMessage());
                return "event/event-details";
            }
        } else {
            return "redirect:/login"; // Si no está autenticado, redirige a login
        }
    }

    @GetMapping("/event/{eventId}/stats")
    public String eventStats(@PathVariable Long eventId, Model model, HttpSession session) {
        String token = (String) session.getAttribute("jwtToken");
        Boolean isAuthenticated = token != null && !token.isEmpty();
        model.addAttribute("authenticated", isAuthenticated);

        if (isAuthenticated) {
            try {
                User user = userService.getAuthenticatedUser(token);

                // Fetch event details
                Event event = eventService.getEventById(token, eventId);
                if (event == null) {
                    model.addAttribute("error", "Evento no encontrado");
                    return "event/details";
                }

                // Fetch total bets for the event
                Long totalBets = betService.getTotalBetsByEvent(token, eventId);

                // Fetch user ranking by correct bets
                List<UserBetStats> userRanking = betService.getUserRankingByCorrectBets(token, eventId);

                // Fetch most popular market
                Market mostPopularMarket = betService.getMostPopularMarketByEvent(token, eventId);

                Option mostPopularOption = betService.getMostPopularOptionByEvent(token, eventId);

                // Fetch bet distribution by market
                Map<String, Map<String, Long>> marketBetDistributions = betService.getBetDistributionByMarket(token, eventId);

                // Add attributes to model
                model.addAttribute("event", event);
                model.addAttribute("totalBets", totalBets);
                model.addAttribute("userRanking", userRanking);
                model.addAttribute("mostPopularMarket", mostPopularMarket);
                model.addAttribute("mostPopularOption", mostPopularOption);
                model.addAttribute("marketBetDistributions", marketBetDistributions);

                return "event/event-stats";
            } catch (Exception e) {
                model.addAttribute("error", "Error al cargar las estadísticas: " + e.getMessage());
                return "event/event-details";
            }
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/event/new")
    public String newEvent(Model model, HttpSession session) {
        String token = (String) session.getAttribute("jwtToken");
        Boolean isAuthenticated = token != null && !token.isEmpty();
        model.addAttribute("authenticated", isAuthenticated);

        if (isAuthenticated) {
            model.addAttribute("categories", categoryService.getAllCategories(token));
        }
        return "event/add-event";
    }

    @PostMapping("/event/new")
    public String saveEvent(
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("closedAtDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate closedAtDate,
            @RequestParam("closedAtTime") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime closedAtTime,
            @RequestParam(value = "urlImage", required = false) String urlImage,
            @RequestParam("category.id") Long categoryId,
            @RequestParam(value = "marketList[].question", required = false) List<String> marketNames,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String token = (String) session.getAttribute("jwtToken");
        Boolean isAuthenticated = token != null && !token.isEmpty();

        if (!isAuthenticated) {
            return "redirect:/login";
        }

        try {
            Event event = new Event();
            event.setTitle(title);
            event.setDescription(description);

            // Construimos el LocalDateTime combinando fecha y hora
            if (closedAtDate != null && closedAtTime != null) {
                LocalDateTime closedAt = LocalDateTime.of(closedAtDate, closedAtTime);
                event.setClosedAt(closedAt);
            } else {
                event.setClosedAt(null);
            }

            event.setUrlImage(urlImage);

            Category category = new Category();
            category.setId(categoryId);
            event.setCategory(category);

            // Crear lista de mercados
            List<Market> markets = new ArrayList<>();
            if (marketNames != null) {
                for (String question : marketNames) {
                    if (!question.isBlank()) {
                        Market market = new Market();
                        market.setQuestion(question);
                        market.setEvent(event); // Asocia el evento si aplica
                        markets.add(market);
                    }
                }
            }

            event.setMarketList(markets);

            // Obtener el userId desde el token
            Long userId = getUserIdFromToken(token);
            eventService.addEvent(token, event, userId);

            redirectAttributes.addFlashAttribute("success", "Evento creado correctamente.");
            return "redirect:/events";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear el evento: " + e.getMessage());
            return "redirect:/event/new";
        }
    }


    @PostMapping("/event/{id}")
    public String updateEvent(
            @PathVariable Long id,
            @RequestParam("closedAtDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate closedAtDate,
            @RequestParam("closedAtTime") @DateTimeFormat(pattern = "HH:mm") LocalTime closedAtTime,
            @ModelAttribute Event event,
            Model model,
            HttpSession session) {

        String token = (String) session.getAttribute("jwtToken");
        Boolean isAuthenticated = token != null && !token.isEmpty();
        model.addAttribute("authenticated", isAuthenticated);

        if (!isAuthenticated) {
            return "redirect:/login"; // Redirige si no autenticado
        }

        try {
            boolean hasPermissions = permissionService.hasPermissions(token, event);

            Long userId = getUserIdFromToken(token);

            // Combinar fecha y hora en LocalDateTime o ajustar según el tipo de closedAt
            if (closedAtDate != null && closedAtTime != null) {
                // Si closedAt es LocalDateTime:
                LocalDateTime closedAt = LocalDateTime.of(closedAtDate, closedAtTime);
                event.setClosedAt(closedAt);
            } else {
                event.setClosedAt(null); // o manejar según convenga
            }

            event.setId(id); // Mantener el ID

            // Actualiza el evento
            eventService.updateEvent(token, event, userId);

            // Recarga el evento actualizado
            Event updatedEvent = eventService.getEventById(token, id);

            model.addAttribute("permisions", hasPermissions);
            model.addAttribute("success", "Evento actualizado con éxito");
            model.addAttribute("event", updatedEvent);
            model.addAttribute("editing", false);
            return "event/event-details";

        } catch (RuntimeException e) {
            model.addAttribute("error", "Error al guardar el evento: " + e.getMessage());
            model.addAttribute("event", event);
            model.addAttribute("editing", true);
            return "event/event-details";
        }
    }

    @PostMapping("/event/close/{id}")
    public String closeEvent(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpSession session) {
        String token = (String) session.getAttribute("jwtToken");
        Boolean isAuthenticated = token != null && !token.isEmpty();
        redirectAttributes.addFlashAttribute("authenticated", isAuthenticated);

        if (isAuthenticated) {
            try {
                eventService.closeEventById(token, id);
                redirectAttributes.addFlashAttribute("success", "Evento cerrado correctamente.");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Error al cerrar el evento: " + e.getMessage());
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Debes estar autenticado para cerrar un evento.");
        }
        return "redirect:/events";
    }

    @PostMapping("/event/delete/{id}")
    public String deleteEvent(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpSession session) {
        String token = (String) session.getAttribute("jwtToken");
        Boolean isAuthenticated = token != null && !token.isEmpty();
        redirectAttributes.addFlashAttribute("authenticated", isAuthenticated);

        if (isAuthenticated) {
            try {
                eventService.deleteEventById(token, id);
                redirectAttributes.addFlashAttribute("success", "Evento eliminado correctamente.");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Error al eliminar el evento: " + e.getMessage());
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Debes estar autenticado para eliminar un evento.");
        }
        return "redirect:/events";
    }
}
