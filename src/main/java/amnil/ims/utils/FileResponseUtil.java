package amnil.ims.utils;

import amnil.ims.dto.response.ApiResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class FileResponseUtil {
    public static ResponseEntity<?> getResponseEntity(byte[] bytes, String fileName) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .body(bytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Something went wrong", e.getMessage()));
        }
    }
}
