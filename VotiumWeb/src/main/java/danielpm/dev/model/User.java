package danielpm.dev.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collection;
import java.util.List;

/**
 * @author danielpm.dev@gmail.com
 */
@Getter
@Setter
public class User{

    private Long id;
    private String username;
    private String password;
    private String email;
    private String urlImage;

    private List<Bet> betList;
    private List<Event> eventList;

    private Role role;

    public User() {
    }
}
