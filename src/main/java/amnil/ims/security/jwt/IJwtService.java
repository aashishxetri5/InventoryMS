package amnil.ims.utils;

import org.springframework.security.core.Authentication;

import javax.crypto.SecretKey;

public interface IJwtService {
    SecretKey getSigningKey();

    String generateToken(Authentication authentication);

    String getEmailFromToken(String authToken);

    boolean validateToken(String authToken);
}
