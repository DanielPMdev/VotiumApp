package danielpm.dev.entity;

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
@Table(name = "CATEGORIES_VOTIUM")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Event> eventList;

    public Category() {
    }
}
