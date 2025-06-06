package danielpm.dev.dto.response.event;

import lombok.Getter;
import lombok.Setter;

/**
 * @author danielpm.dev
 */
@Getter
@Setter
public class SumaryEventDTO {

    private Long id;

    private String title;

    public SumaryEventDTO(Long id, String title) {
        this.id = id;
        this.title = title;
    }
}
