package danielpm.dev.controller;

import danielpm.dev.dto.form.MarketForm;
import danielpm.dev.model.Event;
import danielpm.dev.model.Market;
import danielpm.dev.model.Option;
import danielpm.dev.service.EventService;
import danielpm.dev.service.MarketService;
import danielpm.dev.service.OptionService;
import danielpm.dev.service.PermissionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author danielpm.dev
 */
@Controller
public class MarketController {

    private final EventService eventService;
    private final MarketService marketService;
    private final PermissionService permissionService;
    private final OptionService optionService;

    public MarketController(EventService eventService, MarketService marketService, PermissionService permissionService, OptionService optionService) {
        this.eventService = eventService;
        this.marketService = marketService;
        this.permissionService = permissionService;
        this.optionService = optionService;
    }

    @GetMapping("/event/{eventId}/markets")
    public String showMarkets(@PathVariable Long eventId, Model model, HttpSession session) {
        String token = (String) session.getAttribute("jwtToken");
        Boolean isAuthenticated = token != null && !token.isEmpty();
        model.addAttribute("authenticated", isAuthenticated);

        if (isAuthenticated) {
            try {
                Event event = eventService.getEventById(token, eventId);

                boolean hasPermissions = permissionService.hasPermissions(token, event);

                model.addAttribute("permisions", hasPermissions);
                model.addAttribute("event", event);
                model.addAttribute("markets", marketService.getMarkets(token, eventId));
                return "market/market-details";
            } catch (Exception e) {
                model.addAttribute("error", "Error al cargar los mercados: " + e.getMessage());
                return "market/market-details";
            }
        } else {
            return "redirect:/login"; // Si no está autenticado, redirige a login
        }
    }

    @PostMapping("/event/{eventId}/markets/new")
    public String addMarket(
            @PathVariable Long eventId,
            @ModelAttribute MarketForm marketForm,
            RedirectAttributes redirectAttributes,
            HttpSession session,
            Model model) {

        String token = (String) session.getAttribute("jwtToken");
        Boolean isAuthenticated = token != null && !token.isEmpty();
        model.addAttribute("authenticated", isAuthenticated);

        if (isAuthenticated) {
            try {
                Market market = new Market();
                market.setQuestion(marketForm.getQuestion());

                List<Option> options = marketForm.getOptionList() == null ? List.of() :
                        marketForm.getOptionList().stream()
                                .filter(optionForm -> optionForm.getText() != null && !optionForm.getText().isBlank())
                                .map(optionForm -> {
                                    Option option = new Option();
                                    option.setText(optionForm.getText());
                                    option.setMarket(market);
                                    return option;
                                })
                                .collect(Collectors.toList());

                marketService.saveMarketWithOptions(token, eventId, market, options);
                redirectAttributes.addFlashAttribute("success", "Mercado añadido correctamente.");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Error al añadir el mercado: " + e.getMessage());
            }
            return "redirect:/event/" + eventId + "/markets";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/event/{eventId}/markets/{marketId}")
    public String editMarket(@PathVariable Long eventId, @PathVariable Long marketId, @RequestParam(value = "editing", required = false)
    Boolean editing, Model model, HttpSession session) {

        String token = (String) session.getAttribute("jwtToken");
        boolean isAuthenticated = token != null && !token.isEmpty();
        model.addAttribute("authenticated", isAuthenticated);

        if (!isAuthenticated) {
            return "redirect:/login";
        }

        try {
            Event event = eventService.getEventById(token, eventId);

            if (event == null) {
                model.addAttribute("error", "Evento no encontrado");
                return "redirect:/events";
            }

            Market market = marketService.getMarketById(token, marketId);
            if (market == null) {
                model.addAttribute("error", "Mercado no encontrado");
                return "redirect:/event/" + eventId + "/markets";
            }

            boolean hasPermissions = permissionService.hasPermissions(token, event);

            model.addAttribute("permisions", hasPermissions);

            // Ensure editMarket has the same options as the market
            model.addAttribute("event", event);
            model.addAttribute("markets", marketService.getMarkets(token, eventId));
            model.addAttribute("editMarket", market); // Market object with options
            if (Boolean.TRUE.equals(editing)) {
                model.addAttribute("editingMarket", marketId);
            }
            return "market/market-details";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el mercado: " + e.getMessage());
            return "redirect:/event/" + eventId + "/markets";
        }
    }

    @PostMapping("/event/{eventId}/markets/{marketId}")
    public String updateMarket(
            @PathVariable Long eventId,
            @PathVariable Long marketId,
            @ModelAttribute("editMarket") Market updatedMarket, // Change to "editMarket" to minted
            RedirectAttributes redirectAttributes,
            HttpSession session,
            Model model) {

        String token = (String) session.getAttribute("jwtToken");
        boolean isAuthenticated = token != null && !token.isEmpty();
        model.addAttribute("authenticated", isAuthenticated);

        if (!isAuthenticated) {
            return "redirect:/login";
        }

        try {
            updatedMarket.setId(marketId);
            Event event = eventService.getEventById(token, eventId);
            if (event == null) {
                redirectAttributes.addFlashAttribute("error", "Evento no encontrado");
                return "redirect:/events";
            }
            updatedMarket.setEvent(event);

            marketService.updateMarket(token, eventId, updatedMarket);

            redirectAttributes.addFlashAttribute("success", "Mercado actualizado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el mercado: " + e.getMessage());
        }

        return "redirect:/event/" + eventId + "/markets";
    }

//    @PostMapping("/event/{eventId}/markets/{marketId}")
//    public String updateMarket(
//            @PathVariable Long eventId,
//            @PathVariable Long marketId,
//            @ModelAttribute("editMarket") Market updatedMarket, // Change to "editMarket" to minted
//            RedirectAttributes redirectAttributes,
//            HttpSession session,
//            Model model) {
//        String token = (String) session.getAttribute("jwtToken");
//        boolean isAuthenticated = token != null && !token.isEmpty();
//        model.addAttribute("authenticated", isAuthenticated);
//
//        if (!isAuthenticated) {
//            return "redirect:/login";
//        }
//
//        try {
//            updatedMarket.setId(marketId);
//            Event event = eventService.getEventById(token, eventId);
//            if (event == null) {
//                redirectAttributes.addFlashAttribute("error", "Evento no encontrado");
//                return "redirect:/events";
//            }
//            updatedMarket.setEvent(event);
//
//            List<Option> options = updatedMarket.getOptionList() != null ? updatedMarket.getOptionList() : new ArrayList<>();
//            options.forEach(option -> option.setMarket(updatedMarket));
//
//            marketService.updateMarketWithOptions(token, eventId, updatedMarket, options);
//            redirectAttributes.addFlashAttribute("success", "Mercado actualizado correctamente.");
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("error", "Error al actualizar el mercado: " + e.getMessage());
//        }
//
//        return "redirect:/event/" + eventId + "/markets";
//    }

    @PostMapping("/event/{eventId}/markets/delete/{marketId}")
    public String deleteMarket(@PathVariable Long eventId, @PathVariable Long marketId,
                               RedirectAttributes redirectAttributes, HttpSession session) {
        String token = (String) session.getAttribute("jwtToken");
        Boolean isAuthenticated = token != null && !token.isEmpty();
        redirectAttributes.addFlashAttribute("authenticated", isAuthenticated);

        if (isAuthenticated) {
            try {
                marketService.deleteMarketById(token, marketId);
                redirectAttributes.addFlashAttribute("success", "Mercado eliminado correctamente.");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Error al eliminar el mercado: " + e.getMessage());
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Debes estar autenticado para eliminar un mercado.");
        }
        return "redirect:/event/" + eventId + "/markets";
    }

}

//    @PostMapping("/event/{eventId}/markets/{marketId}")
//    public String updateMarket(
//            @PathVariable Long eventId,
//            @PathVariable Long marketId,
//            @ModelAttribute("market") Market updatedMarket,
//            RedirectAttributes redirectAttributes,
//            HttpSession session,
//            Model model) {
//
//        String token = (String) session.getAttribute("jwtToken");
//        boolean isAuthenticated = token != null && !token.isEmpty();
//        model.addAttribute("authenticated", isAuthenticated);
//
//        if (!isAuthenticated) {
//            return "redirect:/login";
//        }
//
//        try {
//            // Asegurar que el Market tenga su ID correcto
//            updatedMarket.setId(marketId);
//
//            // Asociar con el evento
//            Event event = new Event();
//            event.setId(eventId);
//            updatedMarket.setEvent(event);
//
//            // Establecer el Market como referencia en cada Option (por si el binding falla)
//            List<Option> options = updatedMarket.getOptionList() != null ? updatedMarket.getOptionList() : List.of();
//            options.forEach(option -> option.setMarket(updatedMarket));
//
//            // Llamada al servicio con Market y opciones por separado
//            marketService.updateMarketWithOptions(token, eventId, updatedMarket, options);
//
//            redirectAttributes.addFlashAttribute("success", "Mercado actualizado correctamente.");
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("error", "Error al actualizar el mercado: " + e.getMessage());
//        }
//
//        return "redirect:/event/{eventId}/markets";
//    }
