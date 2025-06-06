package danielpm.dev.dto.response.bet;

import danielpm.dev.dto.response.option.SumaryOptionDTO;
import danielpm.dev.entity.BetStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * @author danielpm.dev
 */
@Getter
@Setter
public class ResponseBetDTO {

    private Long id;

    private LocalDate betDate;
    private BetStatus status;

    private SumaryOptionDTO option;

    public ResponseBetDTO(Long id, LocalDate betDate, BetStatus status, SumaryOptionDTO option) {
        this.id = id;
        this.betDate = betDate;
        this.status = status;
        this.option = option;
    }
}
