package danielpm.dev.dto.request.market;

import danielpm.dev.dto.request.option.CreateOptionDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author danielpm.dev
 */
@Getter
@Setter
public class MarketOptionsDTO {

    private Long id; // Se puede omitir en el body, pero se comprueba en el controlador

    private String question;


    private Long eventId;

    private List<CreateOptionDTO> options;
}
