package amnil.ims.dto.response;

import amnil.ims.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private Long orderId;

    private BigDecimal totalAmount;

    private OrderStatus status;

    private List<ProductResponse> products;

    @Data
    @Builder
    public static class ProductResponse {
        private int quantity;
        private Long productId;
        private String productName;
        private BigDecimal rate;
        private BigDecimal totalPrice;
    }

}
