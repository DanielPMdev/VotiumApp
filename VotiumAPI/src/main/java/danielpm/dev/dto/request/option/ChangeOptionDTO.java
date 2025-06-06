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
public class ChangeOptionDTO {

    private Long id;

    private String text;
//    @JsonProperty("winner")
//    private Boolean isWinner;
//
//    private Double percentage;



    // Default constructor
    public ChangeOptionDTO() {

    }

    public ChangeOptionDTO(Option option) {
        this.id = option.getId();

        this.text = option.getText();
    }

}
