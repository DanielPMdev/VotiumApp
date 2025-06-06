package danielpm.dev.dto.request.market;

import danielpm.dev.entity.Market;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * @author danielpm.dev
 */
@Getter
@Setter
public class CreateMarketDTO {
    private Long id;

    private String question;
    private Long eventId;

    // Default constructor
    public CreateMarketDTO() {}

    public CreateMarketDTO(Market market) {
        this.id = market.getId();

        this.question = market.getQuestion();
        this.eventId = market.getEvent().getId();
    }

}
