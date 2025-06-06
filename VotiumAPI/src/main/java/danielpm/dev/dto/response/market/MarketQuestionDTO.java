package danielpm.dev.dto.response.market;

import danielpm.dev.entity.Market;
import lombok.Getter;
import lombok.Setter;

/**
 * @author danielpm.dev
 */
@Getter
@Setter
public class MarketQuestionDTO {

    private Long id;

    private String question;

    public MarketQuestionDTO() {
    }

    public MarketQuestionDTO(Market market) {
        this.id = market.getId();
        this.question = market.getQuestion();
    }
}
