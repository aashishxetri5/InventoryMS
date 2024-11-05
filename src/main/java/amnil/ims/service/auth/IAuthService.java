package amnil.ims.service.auth;

import amnil.ims.dto.auth.LoginRequest;
import amnil.ims.dto.auth.SignupRequest;
import amnil.ims.dto.response.UserResponse;

public interface IAuthService {
    UserResponse authenticateUser(LoginRequest request);

    UserResponse registerUser(SignupRequest request);
}
