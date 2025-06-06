package danielpm.dev.dto.request.bet;

import danielpm.dev.entity.Bet;
import lombok.Getter;
import lombok.Setter;

/**
 * @author danielpm.dev
 */
@Getter
@Setter
public class ChangeBetDTO {

    private Long id;

    //private LocalDate betDate;
    //private BetStatus status;

    private Long optionId;
    private Long userId;

    // Default constructor
    public ChangeBetDTO() {
    }

//    public CreateBetDTO(Bet bet) {
//
//    }
}
