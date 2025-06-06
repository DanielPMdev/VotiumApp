package danielpm.dev.dto.request.bet;

import danielpm.dev.entity.Bet;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * @author danielpm.dev
 */
@Getter
@Setter
public class CreateBetDTO {
    private Long id;

    //private LocalDate betDate;
    //private BetStatus status;
    
    private Long optionId;
    private Long userId;

    // Default constructor
    public CreateBetDTO() {
        
    }

    public CreateBetDTO(Bet bet) {
        this.id = bet.getId();

        this.optionId = bet.getOption().getId();
        this.userId = bet.getUser().getId();
    }

}
