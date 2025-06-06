package danielpm.dev.controller;

import danielpm.dev.model.Bet;
import danielpm.dev.model.Event;
import danielpm.dev.model.Market;
import danielpm.dev.model.User;
import danielpm.dev.service.BetService;
import danielpm.dev.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author danielpm.dev
 */
@Controller
public class BetController {

    private final BetService betService;
    private final UserService userService;

    public BetController(BetService betService, UserService userService) {
        this.betService = betService;
        this.userService = userService;
    }

    @GetMapping("/myBets")
    public String userBets(Model model, HttpSession session) {
        String token = (String) session.getAttribute("jwtToken");
        Boolean isAuthenticated = token != null && !token.isEmpty();
        model.addAttribute("authenticated", isAuthenticated);

        if (isAuthenticated) {
            try {
                User user = userService.getAuthenticatedUser(token);

                List<Bet> betList = betService.getUserBets(token, user.getId());

                Map<Event, List<Bet>> groupedBets = betList.stream()
                        .collect(Collectors.groupingBy(bet -> bet.getOption().getMarket().getEvent()));

                model.addAttribute("groupedBets", groupedBets);
                return "bet/myBets";
            } catch (Exception e) {
                model.addAttribute("error", "Error al cargar las apuestas: " + e.getMessage());
                return "bet/myBets";
            }
        } else {
            return "redirect:/login"; // Si no está autenticado, redirige a login
        }
    }

    @GetMapping("/myBets/stats")
    public String userBetStats(Model model, HttpSession session) {
        String token = (String) session.getAttribute("jwtToken");
        Boolean isAuthenticated = token != null && !token.isEmpty();
        model.addAttribute("authenticated", isAuthenticated);

        if (isAuthenticated) {
            try {
                User user = userService.getAuthenticatedUser(token);

                Map<String, Long> countByStatus = betService.getCountBetsByStatus(token, user.getId());

                double winRatio = calculateWinRatio(countByStatus);

                Map<String, Long> countByDate = betService.getCountBetsByDate(token, user.getId());

                model.addAttribute("countByStatus", countByStatus);
                model.addAttribute("winRatio", winRatio);
                model.addAttribute("countByDate", countByDate);
                return "bet/stats";
            } catch (Exception e) {
                model.addAttribute("error", "Error al cargar las estadisticas: " + e.getMessage());
                return "bet/myBets";
            }
        } else {
            return "redirect:/login"; // Si no está autenticado, redirige a login
        }
    }

    @PostMapping("/bets/cancel/{id}")
    public String cancelBet(@PathVariable Long id, HttpSession session, Model model) {
        String token = (String) session.getAttribute("jwtToken");
        Boolean isAuthenticated = token != null && !token.isEmpty();
        model.addAttribute("authenticated", isAuthenticated);

        if (isAuthenticated) {
            try {
                betService.deleteBetById(token, id);
                return "redirect:/myBets?message=Apuesta cancelada correctamente";
            } catch (Exception e) {
                model.addAttribute("error", "Error al cancelar la apuesta: " + e.getMessage());
                return "redirect:/myBets";
            }
        } else {
            return "redirect:/login";
        }
    }

    //METHODS AUX

    public double calculateWinRatio(Map<String, Long> countByStatus) {
        long total = countByStatus.entrySet().stream()
                .filter(entry -> !entry.getKey().equals("PENDING")) // Excluir PENDING
                .mapToLong(Map.Entry::getValue)
                .sum();

        if (total == 0) return 0; // Evitar división por cero

        long wins = countByStatus.getOrDefault("WIN", 0L);

        double ratio = (double) wins / total * 100;

        // Redondear a 2 decimales
        BigDecimal rounded = BigDecimal.valueOf(ratio).setScale(2, RoundingMode.HALF_UP);
        return rounded.doubleValue();
    }

}