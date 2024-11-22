package amnil.ims.dto.request;

import amnil.ims.model.Supplier;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {

    private Long productId;

    @NotNull
    private String productName;

    @NotNull
    private String description;

    @NotNull
    private BigDecimal price;

    @NotNull
    @Min(value = 1, message = "Quantity must be greater than 0")
    private int quantity;

    @NotNull
    private Supplier supplier;

    public ProductRequest(String productName, String description, BigDecimal price, int quantity, Supplier supplier) {
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.supplier = supplier;
    }
}
