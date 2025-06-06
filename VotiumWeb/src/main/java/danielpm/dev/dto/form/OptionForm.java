package danielpm.dev.dto.form;

import lombok.Getter;
import lombok.Setter;

/**
 * @author danielpm.dev
 */
@Getter
@Setter
public class OptionForm {
    private String text;

    public OptionForm() {
    }

    public OptionForm(String text) {
        this.text = text;
    }
}