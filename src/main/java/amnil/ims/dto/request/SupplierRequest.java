package amnil.ims.dto.request;

import amnil.ims.enums.Status;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SupplierRequest {
    @NotNull(message = "supplier cannot be null")
    private String supplierName;

    @NotNull(message = "email cannot be null")
    private String email;

    @NotNull(message = "phone number cannot be null")
    private String phoneNumber;

    @NotNull(message = "status cannot be null")
    private Status status;
}
