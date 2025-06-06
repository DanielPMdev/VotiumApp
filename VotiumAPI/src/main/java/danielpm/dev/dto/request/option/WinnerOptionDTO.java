package danielpm.dev.dto.request.option;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author danielpm.dev
 */
@Getter
@Setter
public class WinnerOptionDTO {

    private Long id;

    @JsonProperty("winner")
    private Boolean isWinner;

    private Long marketId;

    public WinnerOptionDTO() {
    }

    public WinnerOptionDTO(Long id, Boolean isWinner, Long marketId) {
        this.id = id;
        this.isWinner = isWinner;
        this.marketId = marketId;
    }
}
