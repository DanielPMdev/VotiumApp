package danielpm.dev.dto.response.option;

import danielpm.dev.dto.response.market.ResponseMarketDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * @author danielpm.dev
 */
@Getter
@Setter
public class SumaryOptionDTO {

    private Long id;

    private Double percentage;
    private String text;
    private ResponseMarketDTO market;

    public SumaryOptionDTO(Long id, Double percentage, String text, ResponseMarketDTO market) {
        this.id = id;
        this.percentage = percentage;
        this.text = text;
        this.market = market;
    }
}
