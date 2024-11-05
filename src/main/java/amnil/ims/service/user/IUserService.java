package amnil.ims.service.user;

import amnil.ims.dto.auth.SignupRequest;
import amnil.ims.dto.response.UserResponse;
import amnil.ims.model.User;

public interface IUserService {
    UserResponse saveNewUser(SignupRequest request);

    User getUserByEmail(String email);
}
