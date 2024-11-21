package amnil.ims.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
public class ProductsBySupplierDto {
    private String productName;
    private BigDecimal price;
    private String supplierName;
}
