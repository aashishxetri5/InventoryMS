package amnil.ims.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SupplierRespose {
    private Long supplierId;

    private String supplierName;

    private String contactInfo;
}
