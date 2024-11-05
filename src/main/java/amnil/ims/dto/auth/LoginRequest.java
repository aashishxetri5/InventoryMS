package amnil.ims.dto.auth;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class LoginRequest {
    @NonNull
    public String email;

    @NonNull
    public String password;

}
