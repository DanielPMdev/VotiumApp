package danielpm.dev.dto.response.option;

import lombok.Getter;
import lombok.Setter;

/**
 * @author danielpm.dev
 */
@Getter
@Setter
public class ResponseOptionDTO{

    private Long id;
    private String text;
    private Boolean isWinner;
    private Double percentage;
    private Long marketId;
    private Long eventId;

    // Constructor
    public ResponseOptionDTO(Long id, String text, Boolean isWinner, Double percentage, Long marketId, Long eventId) {
        this.id = id;
        this.text = text;
        this.isWinner = isWinner;
        this.percentage = percentage;
        this.marketId = marketId;
        this.eventId = eventId;
    }
}
