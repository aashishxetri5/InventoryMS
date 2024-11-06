package amnil.ims.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductResponse {

    private Long productId;

    private String productName;

    private String description;

    private BigDecimal price;

    private int quantity;

    private String supplierName;
}
