package amnil.ims.dto.request;

import amnil.ims.enums.OrderStatus;
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

    private List<OrderItemRequest> orderedItems;

    @Data
    @AllArgsConstructor
    @Builder
    public static class OrderItemRequest {
        private Long productId;
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
