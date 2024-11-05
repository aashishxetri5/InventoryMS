package amnil.ims.service.user;

import amnil.ims.dto.auth.SignupRequest;
import amnil.ims.dto.response.UserResponse;
import amnil.ims.exception.DuplicateRecordException;
import amnil.ims.exception.NotFoundException;
import amnil.ims.model.User;
import amnil.ims.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse saveNewUser(SignupRequest request) {

        User user = convertToUser(request);

        return Optional.of(user)
                .filter(
                        existingUser -> !userRepository.existsByEmail(user.getEmail())
                )
                .map(userRepository::save)
                .map(this::convertToResponse)
                .orElseThrow(
                        () -> new DuplicateRecordException("User Already Exists!!")
                );
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private User convertToUser(SignupRequest request) {
        return new User(
                request.getFullname(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getRoles()
        );
    }

    private UserResponse convertToResponse(User user) {
        return new UserResponse(
                user.getUserId(),
                user.getFullname(),
                user.getEmail(),
                user.getRoles()
        );
    }
}
