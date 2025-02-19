package amnil.ims.controller;

import amnil.ims.dto.auth.LoginRequest;
import amnil.ims.dto.auth.SignupRequest;
import amnil.ims.dto.response.ApiResponse;
import amnil.ims.dto.response.UserResponse;
import amnil.ims.service.auth.IAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    @PreAuthorize("isAnonymous()")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        if (request == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("error", "Email and passwords cannot be null"));
        }

        UserResponse response = authService.authenticateUser(request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse("token", response));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        if (request == null) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse("error", "All data needs to be filled."));
        }

        UserResponse response = authService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse("User created Successfully", response));
    }
}
