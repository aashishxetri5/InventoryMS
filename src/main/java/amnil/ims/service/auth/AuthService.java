package amnil.ims.service.auth;

import amnil.ims.dto.auth.LoginRequest;
import amnil.ims.dto.auth.SignupRequest;
import amnil.ims.dto.response.UserResponse;
import amnil.ims.enums.Role;
import amnil.ims.model.User;
import amnil.ims.security.jwt.IJwtService;
import amnil.ims.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final IJwtService jwtService;


    @Override
    public UserResponse authenticateUser(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwtToken = jwtService.generateToken(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Set<Role> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(Role::fromString)
                .collect(Collectors.toSet());

        User user = userService.getUserByEmail(userDetails.getUsername());

        return UserResponse.builder()
                .userId(user.getUserId())
                .fullname(user.getFullname())
                .email(user.getEmail())
                .roles(roles)
                .jwtToken(jwtToken)
                .build();
    }

    @Override
    public UserResponse registerUser(SignupRequest request) {
        return userService.saveNewUser(request);
    }
}
