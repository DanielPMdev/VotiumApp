package danielpm.dev.dto.form;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author danielpm.dev
 */
@Getter
@Setter
public class MarketForm {
    private String question;
    private List<OptionForm> optionList = new ArrayList<>();

    public MarketForm() {
    }

    public MarketForm(String question, List<OptionForm> optionList) {
        this.question = question;
        this.optionList = optionList;
    }
}