package danielpm.dev.service;

import danielpm.dev.dto.response.bet.UserBetStats;
import danielpm.dev.entity.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author danielpm.dev
 */
public interface BetService {
    
    boolean existsById(Long id);

    //Methods retrive
    List<Bet> getAllBets();

    Optional<Bet> getBetById(Long id);

    Optional<List<Bet>> getBetsByStatus(BetStatus betStatus);

    Optional<List<Bet>> getBetsByVoteDate(LocalDate voteDate);

    Optional<List<Bet>> getBetsByUser(User user);

    Optional<List<Bet>> getBetsByUserId(Long userId);

    Optional <Map<String, Long>> getCountUserBetsByStatus(Long userId);

    Optional<Map<String, Long>> getCountUserBetsByDate(Long userId);

    Long getTotalBetsByEvent(Long eventId);

    Optional<List<UserBetStats>> getUserRankingByCorrectBets(Long eventId);

    Optional<Market> getMostPopularMarketByEvent(Long eventId);

    Optional<Option> getMostPopularOptionByEvent(Long eventId);

    Optional<Map<String, Map<String, Long>>> getBetDistributionByMarket(Long eventId);

    Optional<List<Bet>> getBetsByOption(Long optionId);

    Optional<List<Bet>> getBetsByUserAndMarket(Long userId, Long marketId);

    Optional<List<Bet>> getBetsByUserAndOption(Long userId, Long optionId);

    //Methods create / update
    Bet createOrUpdateBet(Bet bet);

    void markBetsAsWin(Long optionId);

    void markBetsAsLose(Long optionId);

    void recalculatePercentages(Long marketId);

    //Methods delete
    void deleteBetById(Long id);

    void deleteAllBets();
    
}
