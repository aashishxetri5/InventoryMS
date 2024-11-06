package amnil.ims.dto.request;

import amnil.ims.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
@AllArgsConstructor
public class SupplierRequest {
    @NonNull
    private String supplierName;

    @NonNull
    private String email;

    @NonNull
    private String phoneNumber;

    @NonNull
    private Status status;
}
