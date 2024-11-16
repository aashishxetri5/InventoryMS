package amnil.ims.exception;

import org.springframework.validation.Errors;

/// This class is useful in scenarios such as invalid input format in CSV files
public class InvalidInputFormatException extends RuntimeException {
    public InvalidInputFormatException(String message) {
        super(message);
    }
}
