package amnil.ims.dto.request;

import amnil.ims.enums.OrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {
    private LocalDate orderDate;

    private OrderStatus orderStatus;

    private List<@Valid OrderItemRequest> orderedItems;

    @Data
    @AllArgsConstructor
    @Builder
    public static class OrderItemRequest {
        private Long productId;
        @Min(value = 1, message = "Quantity must be a positive value.")
        private int quantity;
    }

    public OrderRequest(OrderStatus orderStatus, List<OrderItemRequest> orderedItems) {
        this.orderStatus = orderStatus;
        this.orderedItems = orderedItems;
    }

    public OrderRequest(List<OrderItemRequest> orderedItems) {
        this.orderedItems = orderedItems;
    }
}
