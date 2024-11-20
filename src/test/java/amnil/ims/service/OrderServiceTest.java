package amnil.ims.service;

import amnil.ims.dto.request.OrderRequest;
import amnil.ims.dto.response.OrderResponse;
import amnil.ims.enums.OrderStatus;
import amnil.ims.model.Order;
import amnil.ims.model.OrderItem;
import amnil.ims.model.Product;
import amnil.ims.model.Supplier;
import amnil.ims.repository.OrderItemRepository;
import amnil.ims.repository.OrderRepository;
import amnil.ims.repository.ProductRepository;
import amnil.ims.service.order.OrderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    public void testCreateOrder_Success() {
        Product product = new Product(1L, "Test Product 1", "Test Product 1 Desc", BigDecimal.valueOf(100.0), 20, new Supplier(1L));
        OrderRequest.OrderItemRequest orderRequest = OrderRequest.OrderItemRequest.builder()
                .productId(1L)
                .quantity(5)
                .build();

        OrderRequest request = new OrderRequest(OrderStatus.PENDING, List.of(orderRequest));
        Order order = Order.builder()
                .orderId(1L)
                .orderItems(List.of(new OrderItem(product, 5, BigDecimal.valueOf(500))))
                .orderDate(LocalDate.now())
                .orderStatus(OrderStatus.PENDING)
                .totalAmount(product.getPrice().multiply(BigDecimal.valueOf(orderRequest.getQuantity())))
                .build();

        Mockito.when(productRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(product));
        Mockito.when(orderRepository.save(Mockito.any(Order.class)))
                .thenReturn(order);

        OrderResponse response = orderService.createOrder(request);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(OrderStatus.PENDING, response.getStatus());
        Assertions.assertEquals(5, response.getProducts().get(0).getQuantity());
        Assertions.assertEquals(BigDecimal.valueOf(500.0), response.getTotalAmount());
    }

    @Test
    public void testCreateOrder_Failure() {
        
    }


}
