package danielpm.dev.service.impl;

import danielpm.dev.dto.response.bet.UserBetStats;
import danielpm.dev.entity.*;
import danielpm.dev.repository.BetRepository;
import danielpm.dev.repository.MarketRepository;
import danielpm.dev.repository.OptionRepository;
import danielpm.dev.service.BetService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author danielpm.dev
 */
@Service
public class BetServiceImpl implements BetService {

    private final BetRepository betRepository;
    private final MarketRepository marketRepository;
    private final OptionRepository optionRepository;

    public BetServiceImpl(BetRepository betRepository, MarketRepository marketRepository, OptionRepository optionRepository) {
        this.betRepository = betRepository;
        this.marketRepository = marketRepository;
        this.optionRepository = optionRepository;
    }

    @Override
    public boolean existsById(Long id) {
        return betRepository.existsById(id);
    }

    @Override
    public List<Bet> getAllBets() {
        return betRepository.findAll();
    }

    @Override
    public Optional<Bet> getBetById(Long id) {
        return betRepository.findById(id);
    }

    @Override
    public Optional<List<Bet>> getBetsByStatus(BetStatus betStatus) {
        return betRepository.findByStatus(betStatus);
    }

    @Override
    public Optional<List<Bet>> getBetsByVoteDate(LocalDate betDate) {
        return betRepository.findByBetDate(betDate);
    }

    @Override
    public Optional<List<Bet>> getBetsByUser(User user) {
        return betRepository.findByUser(user);
    }

    @Override
    public Optional<List<Bet>> getBetsByOption(Long optionId) {
        return betRepository.findAllByOption_Id(optionId);
    }

    @Override
    public Optional<List<Bet>> getBetsByUserId(Long userId) {
        return betRepository.findByUserId(userId);
    }

    @Override
    public Optional<Map<String, Long>> getCountUserBetsByStatus(Long userId) {
        Optional<List<Bet>> optionalBets = getBetsByUserId(userId);

        if (optionalBets.isEmpty() || optionalBets.get().isEmpty()) {
            return Optional.empty();
        }

        List<Bet> allBets = optionalBets.get();

        Map<String, Long> countByStatus = allBets.stream()
                .collect(Collectors.groupingBy(
                        bet -> bet.getStatus().name(),
                        Collectors.counting()
                ));

        return Optional.of(countByStatus);
    }

    @Override
    public Optional<Map<String, Long>> getCountUserBetsByDate(Long userId) {
        List<Bet> bets = betRepository.findByUserId(userId).orElse(Collections.emptyList());

        if (bets.isEmpty()) {
            return Optional.empty();
        }

        Map<String, Long> result = bets.stream()
                .filter(bet -> bet.getBetDate() != null)
                .collect(Collectors.groupingBy(
                        bet -> bet.getBetDate().toString(),
                        TreeMap::new,
                        Collectors.counting()
                ));

        return Optional.of(result);
    }

    @Override
    public Long getTotalBetsByEvent(Long eventId) {
        List<Market> markets = marketRepository.findAllByEventId((eventId));
        return markets.stream()
                .flatMap(market -> market.getOptionList().stream())
                .mapToLong(option -> option.getBetList().size())
                .sum();
    }

    @Override
    public Optional<List<UserBetStats>> getUserRankingByCorrectBets(Long eventId) {
        if (eventId == null || eventId < 0) {
            return Optional.empty();
        }

        // 1. Obtener todas las apuestas del evento
        List<Market> markets = marketRepository.findAllByEventId(eventId);
        List<Bet> bets = markets.stream()
                .flatMap(market -> market.getOptionList().stream())
                .flatMap(option -> option.getBetList().stream())
                .toList();

        if (bets.isEmpty()) {
            return Optional.empty();
        }

        // 2. Agrupar las apuestas por usuario
        Map<String, List<Bet>> betsByUser = bets.stream()
                .collect(Collectors.groupingBy(bet -> bet.getUser().getUsername()));

        // 3. Construir la lista de UserBetStats
        List<UserBetStats> userStats = betsByUser.entrySet().stream()
                .map(entry -> {
                    String username = entry.getKey();
                    List<Bet> userBets = entry.getValue();

                    long totalBets = userBets.size();
                    long correctBets = userBets.stream()
                            .filter(bet -> bet.getOption().getIsWinner() != null && bet.getOption().getIsWinner())
                            .count();

                    return new UserBetStats(username, totalBets, correctBets);
                })
                .sorted(Comparator.comparingLong(UserBetStats::getCorrectBets).reversed())
                .toList();

        return Optional.of(userStats);
    }

    @Override
    public Optional<Market> getMostPopularMarketByEvent(Long eventId) {
        List<Market> markets = marketRepository.findAllByEventId(eventId);

        return markets.stream()
                .max(Comparator.comparingLong(market ->
                        market.getOptionList().stream()
                                .mapToLong(option -> option.getBetList().size())
                                .sum()
                ));
    }

    @Override
    public Optional<Option> getMostPopularOptionByEvent(Long eventId) {
        List<Market> markets = marketRepository.findAllByEventId(eventId);


        return markets.stream()
                .flatMap(market -> market.getOptionList().stream())
                .max(Comparator.comparingLong((Option o) -> o.getBetList().size())
                        .thenComparingLong(Option::getId));
    }


    @Override
    public Optional<Map<String, Map<String, Long>>> getBetDistributionByMarket(Long eventId) {
        List<Market> markets = marketRepository.findAllByEventId(eventId);
        if (markets.isEmpty()) {
            return Optional.empty();
        }

        Map<String, Map<String, Long>> distributionMap = markets.stream()
                .collect(Collectors.toMap(
                        market -> market.getQuestion(), // O usa market.getId().toString()
                        market -> market.getOptionList().stream()
                                .collect(Collectors.toMap(
                                        Option::getText,
                                        option -> (long) option.getBetList().size()
                                ))
                ));

        return Optional.of(distributionMap);
    }


    @Override
    public Optional<List<Bet>> getBetsByUserAndMarket(Long userId, Long marketId) {
        return betRepository.findByUserIdAndOptionMarketId(userId, marketId);
    }

    @Override
    public Optional<List<Bet>> getBetsByUserAndOption(Long userId, Long optionId) {
        return betRepository.findByUserIdAndOptionMarketId(userId, optionId);
    }

    @Override
    public Bet createOrUpdateBet(Bet bet) {
        betRepository.save(bet);
        return bet;
    }

    @Override
    public void deleteBetById(Long id) {
        betRepository.deleteById(id);
    }

    @Override
    public void deleteAllBets() {
        betRepository.deleteAll();
    }

    @Override
    public void markBetsAsWin(Long optionId) {
        getBetsByOption(optionId)
                .ifPresent(bets -> {
                    List<Bet> updated = bets.stream()
                            .peek(bet -> bet.setStatus(BetStatus.WIN))
                            .toList();
                    betRepository.saveAll(updated);
                });
    }

    @Override
    public void markBetsAsLose(Long optionId) {
        getBetsByOption(optionId)
                .ifPresent(bets -> {
                    List<Bet> updated = bets.stream()
                            .peek(bet -> bet.setStatus(BetStatus.LOST))
                            .toList();
                    betRepository.saveAll(updated);
                });
    }

    @Override
    public void recalculatePercentages(Long marketId) {
        // 1️⃣ Obtener todas las opciones del Market
        Market market = marketRepository.findById(marketId)
                .orElseThrow(() -> new RuntimeException("Mercado no encontrado"));

        List<Option> options = market.getOptionList();

        // 2️⃣ Contar el total de bets del Market
        int totalBets = 0;
        for (Option opt : options) {
            int count = betRepository.countByOptionId(opt.getId());
            totalBets += count;
        }

        // 3️⃣ Calcular el porcentaje de cada opción
        for (Option opt : options) {
            int count = betRepository.countByOptionId(opt.getId());
            double percentage = totalBets == 0 ? 0.0 : (double) count * 100 / totalBets;
            opt.setPercentage(percentage);
        }

        // 4️⃣ Guardar los cambios
        optionRepository.saveAll(options);
    }
}
