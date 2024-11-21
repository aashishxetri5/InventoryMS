package amnil.ims.service;

import amnil.ims.dto.request.OrderRequest;
import amnil.ims.dto.response.OrderResponse;
import amnil.ims.enums.OrderStatus;
import amnil.ims.exception.CSVExportException;
import amnil.ims.exception.CSVImportException;
import amnil.ims.exception.InsufficientQuantityException;
import amnil.ims.exception.NotFoundException;
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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    //    Test 1: Create Order
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

        when(productRepository.findById(anyLong()))
                .thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class)))
                .thenReturn(order);

        OrderResponse response = orderService.createOrder(request);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(OrderStatus.PENDING, response.getStatus());
        Assertions.assertEquals(5, response.getProducts().get(0).getQuantity());
        Assertions.assertEquals(BigDecimal.valueOf(500.0), response.getTotalAmount());
    }

    @Test
    public void testCreateOrder_NotFound_Failure() {
        OrderRequest.OrderItemRequest orderRequest = OrderRequest.OrderItemRequest.builder()
                .productId(1L)
                .quantity(5)
                .build();
        OrderRequest request = new OrderRequest(OrderStatus.PENDING, List.of(orderRequest));

        when(productRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> orderService.createOrder(request));

        assertEquals("Product not found", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    public void testCreateOrder_NotEnoughQuantity_Failure() {
        Product product = new Product(1L, "Test Product 1", "Test Product 1 Desc", BigDecimal.valueOf(100.0), 20, new Supplier(1L));
        OrderRequest.OrderItemRequest orderRequest = OrderRequest.OrderItemRequest.builder()
                .productId(1L)
                .quantity(50)
                .build();
        OrderRequest request = new OrderRequest(OrderStatus.PENDING, List.of(orderRequest));

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        InsufficientQuantityException exception = assertThrows(InsufficientQuantityException.class,
                () -> orderService.createOrder(request));

        assertEquals("Requested quantity exceeds available quantity", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }


    //    Test 2: getAllOrders
    @Test
    public void testGetAllOrders_Success() {
        OrderItem item1 = OrderItem.builder()
                .id(1L)
                .product(Product.builder().productId(1L).price(BigDecimal.valueOf(1000.0)).build())
                .quantity(5)
                .price(BigDecimal.valueOf(5000.0))
                .build();
        OrderItem item2 = OrderItem.builder()
                .id(2L)
                .product(Product.builder().productId(2L).price(BigDecimal.valueOf(50.0)).build())
                .quantity(5)
                .price(BigDecimal.valueOf(250.0))
                .build();
        OrderItem item3 = OrderItem.builder()
                .id(3L)
                .product(Product.builder().productId(3L).price(BigDecimal.valueOf(2500.0)).build())
                .quantity(1)
                .price(BigDecimal.valueOf(2500.0))
                .build();
        List<Order> orders = List.of(
                new Order(1L, LocalDate.now(), OrderStatus.PENDING, BigDecimal.valueOf(5250.0), List.of(item1, item2)),
                new Order(2L, LocalDate.now(), OrderStatus.COMPLETED, BigDecimal.valueOf(2500.0), List.of(item3))
        );

        when(orderRepository.findAll()).thenReturn(orders);
        List<OrderResponse> response = orderService.getAllOrders();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(2, response.size());
        Assertions.assertEquals(OrderStatus.PENDING, response.get(0).getStatus());
        Assertions.assertEquals(OrderStatus.COMPLETED, response.get(1).getStatus());
        Assertions.assertEquals(orders.get(0).getTotalAmount(), response.get(0).getTotalAmount());

        verify(orderRepository, times(1)).findAll();
    }

    @Test
    public void testGetAllOrders_Empty() {
        when(orderRepository.findAll()).thenReturn(List.of());
        List<OrderResponse> response = orderService.getAllOrders();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(0, response.size());

        verify(orderRepository, times(1)).findAll();
    }


    //    Test 3: importOrdersFromCsv
    @Test
    public void testImportProductsFromCsv_Success() {
        Product product1 = Product.builder().productId(1L).quantity(10).build();
        Product product2 = Product.builder().productId(2L).quantity(5).build();
        Product product10 = Product.builder().productId(10L).quantity(12).build();

        String contents = """
                ID,Order Date,Status,TotalAmount,ProductId,ProductName,ProductDesc,Price,Quantity,
                1,11/14/2024,PENDING,500,10,Prod_A,Good Prod_A,500,1
                1,11/14/2024,PENDING,1000,2,Prod_B,Good Prod_B,500,2
                2,10/15/2024,IN_PROGRESS,2500,1,Prod_C,Best Prod_C,500,5
                """;
        MockMultipartFile file = new MockMultipartFile(
                "file", "orders.csv", "texts/csv", contents.getBytes()
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product2));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product10));

        int importedProductsFromCsv = orderService.importOrdersFromCsv(file);

        assertEquals(3, importedProductsFromCsv);
        verify(productRepository, atLeastOnce()).findById(anyLong());
    }

    @Test
    public void testImportProductsFromCsv_NotFound() {
        String contents = """
                ID,Order Date,Status,TotalAmount,ProductId,ProductName,ProductDesc,Price,Quantity,
                1,11/14/2024,PENDING,500,10,Prod_A,Good Prod_A,500,1
                1,11/14/2024,PENDING,1000,2,Prod_B,Good Prod_B,500,2
                2,10/15/2024,IN_PROGRESS,2500,1,Prod_C,Best Prod_C,500,5
                """;
        MockMultipartFile file = new MockMultipartFile(
                "file", "orders.csv", "texts/csv", contents.getBytes()
        );

        when(productRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        CSVImportException exception = assertThrows(CSVImportException.class, () -> {
            orderService.importOrdersFromCsv(file);
        });

        assertTrue(exception.getMessage().contains("Product not found: " + 1L));
        verify(productRepository, atMostOnce()).findById(anyLong());
        verify(orderRepository, never()).saveAll(anyList());
    }

    @Test
    public void testImportProductsFromCsv_InsufficientQuantity() {
        Product product1 = Product.builder().productId(1L).productName("Prod_A").quantity(5).build();
        Product product2 = Product.builder().productId(2L).productName("Prod_B").quantity(5).build();
        Product product10 = Product.builder().productId(10L).productName("Prod_C").quantity(0).build();

        String contents = """   
                ID,Order Date,Status,TotalAmount,ProductId,ProductName,ProductDesc,Price,Quantity,
                1,11/14/2024,PENDING,500,10,Prod_A,Good Prod_A,500,1
                1,11/14/2024,PENDING,1000,2,Prod_B,Good Prod_B,500,2
                2,10/15/2024,IN_PROGRESS,2500,1,Prod_C,Best Prod_C,500,5
                """;
        MockMultipartFile file = new MockMultipartFile(
                "file", "orders.csv", "texts/csv", contents.getBytes()
        );

        when(productRepository.findById(10L)).thenReturn(Optional.of(product10));

        CSVImportException exception = assertThrows(CSVImportException.class, () -> {
            orderService.importOrdersFromCsv(file);
        });

        System.out.println(exception.getMessage());
        assertTrue(exception.getMessage().contains("Error processing CSV file Insufficient stock for product Prod_C for orderId: " + 1L));
        verify(productRepository, atLeastOnce()).findById(anyLong());
    }


    //    Test 4: exportOrdersToCsv
    @Test
    public void testExportToCsv_Success() {
        OrderItem item1 = OrderItem.builder()
                .id(1L)
                .product(Product.builder().productId(1L).build())
                .quantity(5)
                .price(BigDecimal.valueOf(5000.0))
                .build();
        OrderItem item2 = OrderItem.builder()
                .id(2L)
                .product(Product.builder().productId(3L).build())
                .quantity(1)
                .price(BigDecimal.valueOf(2500.0))
                .build();

        List<Order> orders = List.of(
                new Order(1L, LocalDate.now(), OrderStatus.PENDING, BigDecimal.valueOf(5000.0), List.of(item1)),
                new Order(2L, LocalDate.now(), OrderStatus.COMPLETED, BigDecimal.valueOf(2500.0), List.of(item2))
        );

        when(orderRepository.findAll()).thenReturn(orders);
        byte[] csvOrdersData = orderService.exportOrdersToCsv();

        assertNotNull(csvOrdersData);
        assertTrue(csvOrdersData.length > 0);
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    public void testExportToCsv_Failure() {
        when(orderRepository.findAll())
                .thenThrow(new CSVExportException("Problem exporting CSV"));

        CSVExportException exception = Assertions.assertThrows(
                CSVExportException.class,
                () -> orderService.exportOrdersToCsv()
        );

        Assertions.assertEquals("Problem exporting CSV", exception.getMessage());
    }


    //    Test 5: deleteOrderById
    @Test
    public void testDeleteOrderById_Success() {
        when(orderRepository.existsById(anyLong()))
                .thenReturn(Boolean.TRUE);
        orderService.deleteOrderById(1L);

        verify(orderRepository).existsById(anyLong());
        verify(orderRepository).deleteById(anyLong());
    }

    @Test
    public void testDeleteProductById_NotFound() {
        when(orderRepository.existsById(anyLong()))
                .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> {
            orderService.deleteOrderById(2L);
        });

        verify(orderRepository).existsById(anyLong());
        verify(orderRepository, never()).deleteById(anyLong());
    }

}
