package danielpm.dev.repository;

import danielpm.dev.entity.Bet;
import danielpm.dev.entity.BetStatus;
import danielpm.dev.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * @author danielpm.dev@gmail.com
 */
public interface BetRepository extends JpaRepository<Bet, Long> {
    Optional<List<Bet>> findByStatus(BetStatus status);

    Optional<List<Bet>> findByUser(User user);

    Optional<List<Bet>> findByBetDate(LocalDate betDate);

    int countByOptionId(Long optionId);

    @Query("SELECT b FROM Bet b WHERE b.user.id = :userId")
    Optional<List<Bet>> findByUserId(@Param("userId") Long userId);

    @Query("SELECT b FROM Bet b WHERE b.user.id = :userId AND b.option.market.id = :marketId")
    Optional<List<Bet>> findByUserIdAndOptionMarketId(@Param("userId") Long userId, @Param("marketId") Long marketId);

    @Query("SELECT b FROM Bet b WHERE b.user.id = :userId AND b.option.id = :optionId")
    Optional<List<Bet>> findByUserIdAndOptionId(@Param("userId") Long userId, @Param("optionId") Long optionId);

    Optional<List<Bet>> findAllByOption_Id(Long optionId);
}
