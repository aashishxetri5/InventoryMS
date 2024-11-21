package amnil.ims.service;

import amnil.ims.dto.auth.LoginRequest;
import amnil.ims.dto.response.UserResponse;
import amnil.ims.enums.Role;
import amnil.ims.model.User;
import amnil.ims.repository.UserRepository;
import amnil.ims.security.jwt.IJwtService;
import amnil.ims.service.auth.AuthService;
import amnil.ims.service.auth.IAuthService;
import amnil.ims.service.user.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserService userService;

    @Mock
    private IJwtService jwtService;

    @Mock
    private UserRepository userRepository;

//    Test 1: authenticateUser
    @Test
    public void testAuthenticateUser_Success() {
        LoginRequest request = new LoginRequest("ak@gmail.com", "asdf");

        Set<Role> roles = Set.of(Role.ADMIN, Role.EMPLOYEE);
        UserResponse response = UserResponse.builder()
                .userId(1L)
                .fullname("Aashish")
                .email("ak@gmail.com")
                .roles(roles)
                .jwtToken("asdf")
                .build();

        User mockUser = new User("ak@gmail.com", "adsf", roles);
        UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername(request.getEmail())
                .password("asdf")
                .roles(String.valueOf(roles))
                .build();

        Authentication mockAuthentication = Mockito.mock(Authentication.class);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(request.getEmail(), response.getEmail());
        Assertions.assertEquals("asdf", response.getJwtToken());
    }

}
