package amnil.ims.dto.auth;

import amnil.ims.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.util.Set;

@Data
@AllArgsConstructor
public class SignupRequest {
    @NonNull
    private String fullname;

    @NonNull
    private String email;

    @NonNull
    private String password;

    @NonNull
    private Set<Role> roles;

}
