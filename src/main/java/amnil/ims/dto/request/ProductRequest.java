package amnil.ims.dto.request;

import amnil.ims.model.Supplier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class ProductRequest {

    private String productName;

    private String description;

    private BigDecimal price;

    private int quantity;

    private Supplier supplier;

}
