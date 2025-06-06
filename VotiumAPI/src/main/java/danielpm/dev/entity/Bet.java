package danielpm.dev.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDate;

/**
 * @author danielpm.dev@gmail.com
 */
@Entity
@Getter
@Setter
@Table(name = "BETS_VOTIUM")
public class Bet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    //INCLUIR LA CANTIDAD Y LA CUOTA ¿?
    private LocalDate betDate;

    @Enumerated(EnumType.STRING)
    private BetStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @ManyToOne
    @JsonBackReference
    private Option option;


}
