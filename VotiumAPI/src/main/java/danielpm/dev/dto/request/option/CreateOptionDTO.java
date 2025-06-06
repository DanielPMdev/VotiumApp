package danielpm.dev.dto.request.option;

import com.fasterxml.jackson.annotation.JsonProperty;
import danielpm.dev.entity.Option;
import lombok.Getter;
import lombok.Setter;

/**
 * @author danielpm.dev
 */
@Getter
@Setter
public class CreateOptionDTO {
    private Long id;

    private String text;
    @JsonProperty("winner")
    private Boolean isWinner;
    private Double percentage;

    private Long marketId;

    // Default constructor
    public CreateOptionDTO() {}

    public CreateOptionDTO(Option option) {
        this.id = option.getId();

        this.text = option.getText();
        this.isWinner = option.getIsWinner();
        this.percentage = option.getPercentage();

        this.marketId = option.getMarket().getId();

    }

}
