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
public class ChangeMarketDTO {

    private Long id;

    private String question;


    // Default constructor
    public ChangeMarketDTO() {

    }

    public ChangeMarketDTO(Market market) {
        this.id = market.getId();
        this.question = market.getQuestion();

    }

}
