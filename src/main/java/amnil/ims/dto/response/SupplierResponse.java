package amnil.ims.dto.response;

import amnil.ims.enums.Status;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SupplierResponse {
    private Long supplierId;

    private String supplierName;

    private String email;

    private String phoneNumber;

    private Status status;
}
