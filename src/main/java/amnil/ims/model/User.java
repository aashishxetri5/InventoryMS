package amnil.ims.model;

import amnil.ims.enums.Role;
import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
@Data
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String fullname;

    private String email;

    private String password;

    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    public User(String fullname, String email, String password, Set<Role> roles) {
        this.fullname = fullname;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }
}
