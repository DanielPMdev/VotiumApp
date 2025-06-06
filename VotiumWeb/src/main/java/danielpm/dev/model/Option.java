package danielpm.dev.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author danielpm.dev@gmail.com
 */
@Getter
@Setter
public class Option {

    private Long id;

    private String text;
    private Boolean isWinner;
    private Double percentage;

    private Market market;

    private List<Bet> betList;

    public Option() {
    }
}


//@Min(value = 0, message = "El porcentaje debe ser mayor o igual a 0")
//@Max(value = 100, message = "El porcentaje debe ser menor o igual a 100")
//INCLUIR VALIDACIONES DE JAKARTA

//Incluir cuota ¿?