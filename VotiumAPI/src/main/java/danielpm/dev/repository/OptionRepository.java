package danielpm.dev.repository;

import danielpm.dev.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author danielpm.dev@gmail.com
 */
public interface OptionRepository extends JpaRepository<Option, Long> {

    Optional<List<Option>> findAllByText(String text);

    Optional<List<Option>> findAllByIsWinner(Boolean isWinner);

    Optional<List<Option>> findAllByMarket_Id(Long marketId);
}
