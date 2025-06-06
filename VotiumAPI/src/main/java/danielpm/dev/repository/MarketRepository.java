package danielpm.dev.repository;

import danielpm.dev.entity.Event;
import danielpm.dev.entity.Market;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author danielpm.dev@gmail.com
 */
public interface MarketRepository extends JpaRepository<Market, Long> {

    Optional<List<Market>> findMarketByQuestion(String question);

    Optional<List<Market>> findMarketByEvent(Event event);

    List<Market> findAllByEventId(Long eventId);
}
