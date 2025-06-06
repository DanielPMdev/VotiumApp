package danielpm.dev.controller;

import danielpm.dev.dto.form.OptionForm;
import danielpm.dev.model.*;
import danielpm.dev.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * @author danielpm.dev
 */
@Controller
public class OptionController {

    private final OptionService optionService;
    private final EventService eventService;
    private final MarketService marketService;
    private final PermissionService permissionService;
    private final UserService userService;
    private final BetService betService;

    public OptionController(OptionService optionService, EventService eventService, MarketService marketService, PermissionService permissionService, UserService userService, BetService betService) {
        this.optionService = optionService;
        this.eventService = eventService;
        this.marketService = marketService;
        this.permissionService = permissionService;
        this.userService = userService;
        this.betService = betService;
    }

    @GetMapping("/event/{eventId}/markets/{marketId}/options")
    public String showMarketsOptions(@PathVariable Long eventId, @PathVariable Long marketId, Model model, HttpSession session) {
        String token = (String) session.getAttribute("jwtToken");
        Boolean isAuthenticated = token != null && !token.isEmpty();
        model.addAttribute("authenticated", isAuthenticated);

        if (isAuthenticated) {
            try {
                // Check if user has voted in this market
                User user = userService.getAuthenticatedUser(token);
                List<Bet> betsInMarket = betService.getBetsByUserAndMarket(token, user.getId(), marketId);
                boolean hasVotedInMarket = !betsInMarket.isEmpty();
                model.addAttribute("hasVotedInMarket", hasVotedInMarket);
                Event event = eventService.getEventById(token, eventId);

                boolean isEventClosed = event.isClosedEvent();

                Market market = marketService.getMarketById(token, marketId);

                boolean hasPermissions = permissionService.hasPermissions(token, event);

                List<Option> optionList = optionService.getOptions(token, marketId);

                boolean hasWinningOption = optionList.stream()
                        .filter(Objects::nonNull)
                        .map(Option::getIsWinner)
                        .filter(Objects::nonNull)
                        .anyMatch(Boolean::booleanValue);

                model.addAttribute("permisions", hasPermissions);
                model.addAttribute("hasWinningOption", hasWinningOption);
                model.addAttribute("isEventClosed", isEventClosed);
                model.addAttribute("event", event);
                model.addAttribute("market", market);
                model.addAttribute("options", optionList);
                return "option/option-details";
            } catch (Exception e) {
                model.addAttribute("error", "Error al cargar los mercados: " + e.getMessage());
                return "event/events";
            }
        } else {
            return "redirect:/login"; // Si no está autenticado, redirige a login
        }
    }

    @PostMapping("/event/{eventId}/markets/{marketId}/options/new")
    public String addOption(
            @PathVariable Long eventId,
            @PathVariable Long marketId,
            @ModelAttribute OptionForm optionForm,
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
            Event event = eventService.getEventById(token, eventId);
            if (event == null) {
                redirectAttributes.addFlashAttribute("error", "Evento no encontrado");
                return "redirect:/events";
            }

            Market market = marketService.getMarketById(token, marketId);
            if (market == null) {
                redirectAttributes.addFlashAttribute("error", "Mercado no encontrado");
                return "redirect:/event/" + eventId + "/markets";
            }

            boolean hasPermissions = permissionService.hasPermissions(token, event);
            if (!hasPermissions) {
                redirectAttributes.addFlashAttribute("error", "No tienes permisos para añadir opciones");
                return "redirect:/event/" + eventId + "/markets/" + marketId;
            }

            Option option = new Option();
            option.setText(optionForm.getText());
            option.setMarket(market);

            optionService.saveOption(token, marketId, option);
            redirectAttributes.addFlashAttribute("success", "Opción añadida correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al añadir la opción: " + e.getMessage());
        }

        return "redirect:/event/" + eventId + "/markets/" + marketId + "/options";
    }

    @PostMapping("/event/{eventId}/markets/{marketId}/options/{optionId}/edit")
    public String updateOption(
            @PathVariable Long eventId,
            @PathVariable Long marketId,
            @PathVariable Long optionId,
            @ModelAttribute OptionForm optionForm,
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
            Event event = eventService.getEventById(token, eventId);
            if (event == null) {
                redirectAttributes.addFlashAttribute("error", "Evento no encontrado");
                return "redirect:/events";
            }

            Market market = marketService.getMarketById(token, marketId);
            if (market == null) {
                redirectAttributes.addFlashAttribute("error", "Mercado no encontrado");
                return "redirect:/event/" + eventId + "/markets";
            }

            boolean hasPermissions = permissionService.hasPermissions(token, event);
            if (!hasPermissions) {
                redirectAttributes.addFlashAttribute("error", "No tienes permisos para editar opciones");
                return "redirect:/event/" + eventId + "/markets/" + marketId;
            }

            Option option = optionService.getOptionById(token, optionId);
            if (option == null) {
                redirectAttributes.addFlashAttribute("error", "Opción no encontrada");
                return "redirect:/event/" + eventId + "/markets/" + marketId;
            }

            option.setText(optionForm.getText());
            option.setMarket(market);

            optionService.updateOption(token, marketId, option);
            redirectAttributes.addFlashAttribute("success", "Opción actualizada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la opción: " + e.getMessage());
        }

        return "redirect:/event/" + eventId + "/markets/" + marketId + "/options";
    }

    @PostMapping("event/{eventId}/markets/{marketId}/options/delete/{optionId}")
    public String deleteMarketOption(@PathVariable Long eventId, @PathVariable Long marketId,
                                     @PathVariable Long optionId, RedirectAttributes redirectAttributes,
                                     HttpSession session) {

        String token = (String) session.getAttribute("jwtToken");
        Boolean isAuthenticated = token != null && !token.isEmpty();
        redirectAttributes.addFlashAttribute("authenticated", isAuthenticated);

        if (isAuthenticated) {
            try {
                optionService.deleteOptionById(token, optionId);
                redirectAttributes.addFlashAttribute("success", "Opción eliminada correctamente.");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Error al eliminar la opción : " + e.getMessage());
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Debes estar autenticado para eliminar una opción.");
        }
        return "redirect:/event/" + eventId + "/markets";
    }

    @PostMapping("/event/{eventId}/markets/{marketId}/options/{optionId}/bet")
    public String placeBet(@PathVariable("eventId") Long eventId, @PathVariable("marketId") Long marketId,
                           @PathVariable("optionId") Long optionId, HttpSession session,
                           RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("jwtToken");
        if (token == null || token.isEmpty() || permissionService.isAdmin(token)) {
            return "redirect:/";
        }

        try {
            User user = userService.getAuthenticatedUser(token);
            Option option = optionService.getOptionById(token, optionId);

//            // Check if user has already voted in this market
//            List<Bet> betsInMarket = betService.getBetsByUserAndMarket(token, user.getId(), marketId);
//            if (!betsInMarket.isEmpty()) {
//                redirectAttributes.addFlashAttribute("error", "Ya has votado en este mercado.");
//                return "redirect:/event/" + eventId + "/markets/" + marketId + "/options";
//            }
//
//            // Check if user has already voted for this specific option (optional, if you want to allow changing votes)
//            List<Bet> betsForOption = betService.getBetsByUserAndOption(token, user.getId(), optionId);
//            if (!betsForOption.isEmpty()) {
//                redirectAttributes.addFlashAttribute("error", "Ya has votado por esta opción.");
//                return "redirect:/event/" + eventId + "/markets/" + marketId + "/options";
//            }

            if (option != null && option.getMarket().getId().equals(marketId) &&
                    option.getMarket().getEvent().getId().equals(eventId)) {
                Bet bet = new Bet();
                bet.setUser(user);
                bet.setOption(option);
                betService.saveBet(token, bet);
                redirectAttributes.addFlashAttribute("success", "Votación registrada correctamente.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Opción no encontrada o no válida.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar la votación: " + e.getMessage());
        }
        return "redirect:/event/" + eventId + "/markets/" + marketId + "/options";
    }

    @PostMapping("/event/{eventId}/markets/{marketId}/options/{optionId}/setWinner")
    public String setOptionAsWinner(@PathVariable Long eventId, @PathVariable Long marketId, @PathVariable Long optionId, HttpSession session, Model model) {
        String token = (String) session.getAttribute("jwtToken");
        Boolean isAuthenticated = token != null && !token.isEmpty();
        model.addAttribute("authenticated", isAuthenticated);

        if (isAuthenticated) {
            try {

                optionService.setOptionAsWinner(token, marketId, optionId);
                return "redirect:/event/" + eventId + "/markets/" + marketId + "/options?success=Opción marcada como ganadora";
            } catch (Exception e) {
                model.addAttribute("error", "Error al marcar la opción como ganadora: " + e.getMessage());
                return "redirect:/event/" + eventId + "/markets/" + marketId + "/options";
            }
        } else {
            return "redirect:/login";
        }
    }
}
