package danielpm.dev.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author danielpm.dev@gmail.com
 */
@Entity
@Getter
@Setter
@Table(name = "EVENTS_VOTIUM")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;
    private String description;
    private LocalDate createdAt;
    private LocalDateTime closedAt;
    private boolean closedEvent;

    @Value("${event.default.image}")
    private String urlImage;

    @ManyToOne //CASCADES SI ES NECESARIO
    @JsonBackReference
    private Category category;

    @ManyToOne
    @JsonBackReference
    private User user;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Market> marketList;

    public Event() {
    }

    public Event(String title, String description, LocalDate createdAt, LocalDateTime closedAt, boolean closedEvent, String urlImage, Category category, User user, List<Market> marketList) {
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.closedAt = closedAt;
        this.closedEvent = closedEvent;
        this.urlImage = urlImage;
        this.category = category;
        this.user = user;
        this.marketList = marketList;
    }
}
