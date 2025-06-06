package danielpm.dev.controller;



import danielpm.dev.dto.request.bet.ChangeBetDTO;
import danielpm.dev.dto.request.bet.CreateBetDTO;
import danielpm.dev.dto.response.bet.ResponseBetDTO;
import danielpm.dev.dto.response.bet.UserBetStats;
import danielpm.dev.dto.response.event.SumaryEventDTO;
import danielpm.dev.dto.response.market.ResponseMarketDTO;
import danielpm.dev.dto.response.option.ResponseOptionDTO;
import danielpm.dev.dto.response.option.SumaryOptionDTO;
import danielpm.dev.entity.*;
import danielpm.dev.service.BetService;
import danielpm.dev.service.EventService;
import danielpm.dev.service.OptionService;
import danielpm.dev.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author danielpm.dev
 */
@RestController
@RequestMapping("/api")
public class BetController {

    private final BetService betService;
    private final OptionService optionService;
    private final UserService userService;

    public BetController(BetService betService, OptionService optionService, UserService userService) {
        this.betService = betService;
        this.optionService = optionService;
        this.userService = userService;
    }

    /*
        GET http://localhost:8080/api/bets
     */
    @GetMapping("/bets")
    public ResponseEntity<List<Bet>> findAll() {
        List<Bet> betList = betService.getAllBets();
        if (betList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(betList);
    }

    /*
        GET http://localhost:8080/api/bet/7
     */
    @GetMapping("/bet/{id}")
    public ResponseEntity<Bet> findById(@PathVariable Long id) {
        if (id < 0) {
            return ResponseEntity.badRequest().build();
        }

        return betService.getBetById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /*
        GET http://localhost:8080/api/bets/by-user/8
     */
    @GetMapping("/bet/by-user/{userId}")
    public ResponseEntity<List<ResponseBetDTO>> findByUserId(@PathVariable Long userId) {
        if (userId < 0) {
            return ResponseEntity.badRequest().build();
        }

        return betService.getBetsByUserId(userId)
                .filter(list -> !list.isEmpty())
                .map(bets -> bets.stream()
                        .map(bet -> {
                            Option option = bet.getOption();
                            Market market = option != null ? option.getMarket() : null;
                            Event event = market != null ? market.getEvent() : null;

                            SumaryEventDTO eventDTO = event != null
                                    ? new SumaryEventDTO(event.getId(), event.getTitle())
                                    : null;

                            ResponseMarketDTO marketDTO = market != null
                                    ? new ResponseMarketDTO(market.getId(), market.getQuestion(), eventDTO)
                                    : null;

                            SumaryOptionDTO optionDTO = option != null
                                    ? new SumaryOptionDTO(option.getId(), option.getPercentage(), option.getText(), marketDTO)
                                    : null;

                            return new ResponseBetDTO(
                                    bet.getId(),
                                    bet.getBetDate(),
                                    bet.getStatus(),
                                    optionDTO
                            );
                        })
                        .collect(Collectors.toList()))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /*
        GET http://localhost:8080/api/bets/by-user/stats/8
    */
    @GetMapping("/bet/by-user/status/{userId}")
    public ResponseEntity<Map<String, Long>> findBetsByUserStats(@PathVariable Long userId) {
        if (userId == null || userId < 0) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Map<String, Long>> statsOpt = betService.getCountUserBetsByStatus(userId);

        return statsOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    /*
        GET http://localhost:8080/api/bet/by-user/date/8
    */
    @GetMapping("/bet/by-user/date/{userId}")
    public ResponseEntity<Map<String, Long>> findBetsByUserDate(@PathVariable Long userId) {
        if (userId == null || userId < 0) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Map<String, Long>> dateStatsOpt = betService.getCountUserBetsByDate(userId);

        return dateStatsOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    /*
        GET http://localhost:8080/api/bet/event/{eventId}/totalBets
    */
    @GetMapping("/bet/event/{eventId}/totalBets")
    public ResponseEntity<Long> findTotalBetsByEvent(@PathVariable Long eventId) {
        if (eventId == null || eventId < 0) {
            return ResponseEntity.badRequest().build();
        }

        Long totalBets = betService.getTotalBetsByEvent(eventId);

        if (totalBets == 0) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(totalBets);
        }
    }

    /*
    GET http://localhost:8080/api/bet/event/{eventId}/user-ranking
    */
    @GetMapping("/bet/event/{eventId}/user-ranking")
    public ResponseEntity<List<UserBetStats>> getUserRankingByCorrectBets(@PathVariable Long eventId) {
        if (eventId == null || eventId < 0) {
            return ResponseEntity.badRequest().build();
        }

        Optional<List<UserBetStats>> rankingOpt = betService.getUserRankingByCorrectBets(eventId);

        return rankingOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    /*
        GET http://localhost:8080/api/bet/event/{eventId}/most-popular-market
    */
    @GetMapping("/bet/event/{eventId}/most-popular-market")
    public ResponseEntity<Market> getMostPopularMarketByEvent(@PathVariable Long eventId) {
        if (eventId == null || eventId < 0) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Market> popularMarketOpt = betService.getMostPopularMarketByEvent(eventId);

        return popularMarketOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    /*
        GET http://localhost:8080/api/bet/event/{eventId}/most-popular-option
    */
    @GetMapping("/bet/event/{eventId}/most-popular-option")
    public ResponseEntity<Option> getMostPopularOptionByEvent(@PathVariable Long eventId) {
        if (eventId == null || eventId < 0) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Option> popularOptionOpt = betService.getMostPopularOptionByEvent(eventId);

        return popularOptionOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    /*
        GET http://localhost:8080/api/bet/event/{eventId}/bet-distribution
    */
    @GetMapping("/bet/event/{eventId}/bet-distribution")
    public ResponseEntity<Map<String, Map<String, Long>>> getBetDistributionByMarket(@PathVariable Long eventId) {
        if (eventId == null || eventId < 0) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Map<String, Map<String, Long>>> betDistributionOpt = betService.getBetDistributionByMarket(eventId);

        return betDistributionOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    /*
         GET http://localhost:8080/api/bets/by-user-and-market?userId={userId}&marketId={marketId}
      */
    @GetMapping("/bets/by-user-and-market")
    public ResponseEntity<List<Bet>> findBetsByUserAndMarket(
            @RequestParam("userId") Long userId,
            @RequestParam("marketId") Long marketId) {
        if (userId < 0 || marketId < 0) {
            return ResponseEntity.badRequest().build();
        }

        return betService.getBetsByUserAndMarket(userId, marketId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /*
        GET http://localhost:8080/api/bets/by-user-and-option?userId={userId}&optionId={optionId}
     */
    @GetMapping("/bets/by-user-and-option")
    public ResponseEntity<List<Bet>> getBetsByUserAndOption(
            @RequestParam("userId") Long userId,
            @RequestParam("optionId") Long optionId) {
        if (userId < 0 || optionId < 0) {
            return ResponseEntity.badRequest().build();
        }

        return betService.getBetsByUserAndOption(userId, optionId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /*
        POST http://localhost:8080/api/bet
     */
    @PostMapping("/bet")
    public ResponseEntity<Bet> createBet(@RequestBody CreateBetDTO betDTO) {
        if (betDTO.getId() != null) //Si ya tiene un id asignado
            return ResponseEntity.badRequest().build();

        //Buscar el Option mediante el ID
        Option option = optionService.getOptionById(betDTO.getOptionId())
                .orElseThrow(() -> new RuntimeException("Option not found"));

        //Buscar el User mediante el ID
        User user = userService.getUserById(betDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Bet betCreated = getBetCreated(betDTO, option, user);

        betService.createOrUpdateBet(betCreated);

        betService.recalculatePercentages(option.getMarket().getId());

        return ResponseEntity.ok(betCreated);
    }

    /*
        PUT http://localhost:8080/api/bet/3
     */
    @PutMapping("/bet/{id}")
    public ResponseEntity<Bet> updateBet(@PathVariable Long id, @RequestBody ChangeBetDTO betDTO) {
        if (!betService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        //Obtener el Beto existente mediante el ID
        Bet betToUpdate = betService.getBetById(id)
                .orElseThrow(() -> new RuntimeException("Bet not found"));

        Bet updatedBet = betService.createOrUpdateBet(betToUpdate);
        return ResponseEntity.ok(updatedBet);
    }

    /*
        DELETE http://localhost:8080/api/bet/{identifier}
     */
    @DeleteMapping("/bet/{identifier}")
    public ResponseEntity<Void> deleteBetById(@PathVariable("identifier") Long id) {
        if (!betService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        Long marketId = betService.getBetById(id)
                .map(bet -> bet.getOption().getMarket().getId())
                .orElseThrow(() -> new RuntimeException("Bet not found"));

        betService.deleteBetById(id);

        betService.recalculatePercentages(marketId);

        return ResponseEntity.noContent().build();
    }

    //AUXILIARY METHODS
    private Bet getBetCreated(CreateBetDTO betDTO, Option option, User user) {
        Bet betCreated = new Bet();

        betCreated.setId(betDTO.getId());
        betCreated.setBetDate(LocalDate.now());
        betCreated.setStatus(BetStatus.PENDING);
        betCreated.setOption(option);
        betCreated.setUser(user);

        return betCreated;
    }
}
