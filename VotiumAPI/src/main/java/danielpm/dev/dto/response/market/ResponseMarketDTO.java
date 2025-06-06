package danielpm.dev.dto.response.market;

import danielpm.dev.dto.response.event.SumaryEventDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * @author danielpm.dev
 */
@Getter
@Setter
public class ResponseMarketDTO {

    private Long id;

    private String question;

    private SumaryEventDTO event;

    public ResponseMarketDTO(Long id, String question, SumaryEventDTO event) {
        this.id = id;
        this.question = question;
        this.event = event;
    }
}
