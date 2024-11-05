package amnil.ims.dto.response;

import amnil.ims.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class UserResponse {
    private Long userId;
    private String fullname;
    private String email;
    private Set<Role> roles;
    private String jwtToken;

    public UserResponse(Long userId, String fullname, String email, Set<Role> roles) {
        this.userId = userId;
        this.fullname = fullname;
        this.email = email;
        this.roles = roles;
    }
}
