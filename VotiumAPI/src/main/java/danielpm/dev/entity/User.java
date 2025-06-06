package danielpm.dev.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * @author danielpm.dev@gmail.com
 */
@Entity
@Getter
@Setter
@Table(name = "USERS_VOTIUM")
public class User implements UserDetails{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    @JsonIgnore //Evitar que la contraseña se incluya en las respuestas JSON
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Bet> betList;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Event> eventList;

    @Value("${user.default.image}")
    private String urlImage;


    //DEMÁS de Spring Security
    @Enumerated(EnumType.STRING)
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    public User(String username, String password, String email, List<Bet> betList, List<Event> eventList, String urlImage, Role role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.betList = betList;
        this.eventList = eventList;
        this.urlImage = urlImage;
        this.role = role;
    }

    public User(String username, String password, String email, List<Bet> betList, List<Event> eventList, String urlImage) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.betList = betList;
        this.eventList = eventList;
        this.urlImage = urlImage;
    }

    public User() {

    }
}
