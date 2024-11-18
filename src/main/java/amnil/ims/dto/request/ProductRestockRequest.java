package amnil.ims.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ProductRestockRequest {
    @NotNull
    private Long productId;

    @NotNull
    @Min(value = 1, message = "Quantity must be greater than 0")
    private int quantity;
}
