package amnil.ims.service;

import amnil.ims.dto.auth.SignupRequest;
import amnil.ims.dto.response.UserResponse;
import amnil.ims.enums.Role;
import amnil.ims.exception.DuplicateRecordException;
import amnil.ims.exception.NotFoundException;
import amnil.ims.model.User;
import amnil.ims.repository.UserRepository;
import amnil.ims.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    //    Test 1: getUserByEmail
    @Test
    public void testGetUserByEmail_Success() {
        String email = "test@test.com";
        User user = User.builder()
                .userId(1L)
                .fullname("Aashish Katwal")
                .email("test@test.com")
                .password(passwordEncoder.encode("password"))
                .roles(Set.of(Role.ADMIN, Role.EMPLOYEE))
                .build();

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        User result = userService.getUserByEmail(email);
        assertNotNull(result);
        assertEquals(email, result.getEmail());
        assertEquals(2, result.getRoles().size());

        verify(passwordEncoder).encode("password");
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    public void testGetUserByEmail_Failure() {
        String email = "test@test.com";
        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.getUserByEmail(email)
        );
        assertEquals("User not found", exception.getMessage());

    }

    //    Test 2: saveNewUser
    @Test
    public void testSaveNewUser_Success() {
        SignupRequest request = new SignupRequest("Aashish Katwal", "ak@gmail.com",
                "asdf", Set.of(Role.ADMIN, Role.EMPLOYEE));
        User user = User.builder()
                .userId(1L)
                .fullname("Aashish Katwal")
                .email("test@test.com")
                .password("asdf")
                .roles(Set.of(Role.ADMIN, Role.EMPLOYEE))
                .build();

        when(userRepository.save(any(User.class))).thenReturn(user);
        UserResponse result = userService.saveNewUser(request);

        assertNotNull(result);
        assert user != null;
        assertEquals(user.getUserId(), 1L);
        assertEquals(user.getFullname(), result.getFullname());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getRoles(), result.getRoles());

        verify(passwordEncoder).encode("asdf");
        verify(userRepository).existsByEmail(anyString());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testSaveNewUser_DuplicateEmail_Failure() {
        SignupRequest request = new SignupRequest("Aashish Katwal", "ak@gmail.com",
                "asdf", Set.of(Role.ADMIN, Role.EMPLOYEE));

        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        assertThrows(DuplicateRecordException.class, () -> {
            userService.saveNewUser(request);
        });

        verify(passwordEncoder).encode("asdf");
        verify(userRepository, times(1)).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

}
