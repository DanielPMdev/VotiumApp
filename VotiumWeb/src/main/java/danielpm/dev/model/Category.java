package danielpm.dev.model;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author danielpm.dev@gmail.com
 */
@Getter
@Setter
public class Category {

    private Long id;

    private String name;

    private List<Event> eventList;

    public Category() {
    }
}
