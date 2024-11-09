package amnil.ims.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ProductsBySupplierDto {
    private String productName;
    private BigDecimal price;
    private String supplierName;
}
