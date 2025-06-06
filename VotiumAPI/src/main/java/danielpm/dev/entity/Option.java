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
@Table(name = "OPTIONS_VOTIUM")
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String text;

    //Incluir cuota ¿?

    private Boolean isWinner;

    //@Min(value = 0, message = "El porcentaje debe ser mayor o igual a 0")
    //@Max(value = 100, message = "El porcentaje debe ser menor o igual a 100")
    //INCLUIR VALIDACIONES DE JAKARTA
    private Double percentage;

    @ManyToOne
    @JsonBackReference
    private Market market;

    @OneToMany(mappedBy = "option", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Bet> betList;

    public Option() {
    }
}
