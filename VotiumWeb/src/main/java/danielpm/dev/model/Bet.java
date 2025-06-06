package danielpm.dev.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * @author danielpm.dev@gmail.com
 */
@Getter
@Setter
public class Bet {

    private Long id;

    private LocalDate betDate;

    private BetStatus status;

    private User user;
    private Option option;

    public Bet() {
    }
}

//INCLUIR LA CANTIDAD Y LA CUOTA ¿?