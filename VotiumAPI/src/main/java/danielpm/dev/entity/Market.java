package danielpm.dev.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author danielpm.dev@gmail.com
 */
@Entity
@Getter
@Setter
@Table(name = "MARKETS_VOTIUM")
public class Market {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String question;

    //INCLUIR SI ESTA PREGUNTA ESTA CERRADA O NO
    //EJEMPLO: EN EL EVENTO DE FUTBOL, TIENE VARIAS PREGUNTAS
    //ESTAS PREGUNTAS PUEDEN QUE YA SE HAYAN CUMPLIDO...

    @ManyToOne //CASCADE SI HACE FALTA, ORPHAN REMOVAL...
    @JsonBackReference
    private Event event;

    @OneToMany (mappedBy = "market", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Option> optionList;

    public Market() {
    }
}
