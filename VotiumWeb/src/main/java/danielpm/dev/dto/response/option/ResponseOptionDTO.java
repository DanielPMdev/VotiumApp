package danielpm.dev.dto.response.option;

import lombok.Getter;
import lombok.Setter;

/**
 * @author danielpm.dev
 */
@Getter
@Setter
public class ResponseOptionDTO {

    private Long id;
    private String text;
    private Boolean isWinner;
    private Double percentage;
    private Long marketId;
    private Long eventId;

    public ResponseOptionDTO() {
    }
}
