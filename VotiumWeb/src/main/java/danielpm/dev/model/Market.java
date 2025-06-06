package danielpm.dev.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author danielpm.dev@gmail.com
 */
@Getter
@Setter
public class Market {

    private Long id;

    private String question;

    private Event event;
    private List<Option> optionList;

    public Market() {
    }
}


//INCLUIR SI ESTA PREGUNTA ESTA CERRADA O NO
//EJEMPLO: EN EL EVENTO DE FUTBOL, TIENE VARIAS PREGUNTAS
//ESTAS PREGUNTAS PUEDEN QUE YA SE HAYAN CUMPLIDO...
