package amnil.ims.service.order;

import amnil.ims.dto.request.OrderRequest;
import amnil.ims.dto.response.OrderResponse;
import amnil.ims.enums.OrderStatus;
import amnil.ims.exception.CSVImportException;
import amnil.ims.exception.InsufficientQuantityException;
import amnil.ims.exception.NotFoundException;
import amnil.ims.model.Order;
import amnil.ims.model.OrderItem;
import amnil.ims.model.Product;
import amnil.ims.repository.OrderItemRepository;
import amnil.ims.repository.OrderRepository;
import amnil.ims.repository.ProductRepository;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class OrderService implements IOrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {
        Order order = orderRepository.save(prepareOrder(orderRequest));
        return convertToOrderResponse(order);
    }

    private OrderResponse convertToOrderResponse(Order order) {

        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .totalAmount(order.getTotalAmount())
                .status(order.getOrderStatus())
                .products(
                        order.getOrderItems().stream()
                                .map(
                                        orderItem -> OrderResponse.ProductResponse
                                                .builder()
                                                .productId(orderItem.getProduct().getProductId())
                                                .productName(orderItem.getProduct().getProductName())
                                                .quantity(orderItem.getQuantity())
                                                .rate(orderItem.getProduct().getPrice())
                                                .totalPrice(orderItem.getPrice())
                                                .build()
                                )
                                .collect(Collectors.toList())
                )
                .build();
    }

    private Order prepareOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderDate(LocalDate.now());
        if (orderRequest.getOrderStatus() == null) {
            order.setOrderStatus(OrderStatus.PENDING);
        } else {
            order.setOrderStatus(orderRequest.getOrderStatus());
        }

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderRequest.OrderItemRequest orderedItems : orderRequest.getOrderedItems()) {
            Product product = productRepository.findById(orderedItems.getProductId())
                    .orElseThrow(() -> new NotFoundException("Product not found"));

            if (product.getQuantity() < orderedItems.getQuantity()) {
                throw new InsufficientQuantityException("Requested quantity exceeds available quantity");
            }

            BigDecimal itemTotalPrice = product.getPrice().multiply(BigDecimal.valueOf(orderedItems.getQuantity()));
            totalAmount = totalAmount.add(itemTotalPrice);

            OrderItem orderItem = new OrderItem(product, orderedItems.getQuantity(), itemTotalPrice);
            orderItems.add(orderItem);

            product.setQuantity(product.getQuantity() - orderedItems.getQuantity());
            productRepository.save(product);
        }

        order.setTotalAmount(totalAmount);
        order.setOrderItems(orderItems);

        return order;
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(
                        this::convertToOrderResponse
                )
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public OrderResponse updateOrder(Long orderId, OrderRequest orderRequest) {
        // Fetch orders for the id. Throw NotFoundException if order doesn't exit.
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        // Fill up new Data (date and orderStatus)
        order.setOrderDate(LocalDate.now());
        order.setOrderStatus(orderRequest.getOrderStatus());

        // track existing items {pid: qty}
        Map<Long, Integer> existingItems = new HashMap<>();
        List<Long> itemsToRemove = new ArrayList<>();
        for (OrderItem item : order.getOrderItems()) {
            existingItems.put(item.getProduct().getProductId(), item.getQuantity());
            itemsToRemove.add(item.getId());
        }

        order.getOrderItems().clear();
        order.setTotalAmount(BigDecimal.ZERO);

        for (OrderRequest.OrderItemRequest newItem : orderRequest.getOrderedItems()) {
            if (newItem.getQuantity() == 0)
                continue;
            Product product = productRepository.findById(newItem.getProductId())
                    .orElseThrow(() -> new NotFoundException("Product not found for id: " + newItem.getProductId()));

            int newQuantity = newItem.getQuantity();
            int existingQuantity = existingItems.getOrDefault(product.getProductId(), 0);
            int quantityDifference = newQuantity - existingQuantity;
            System.out.println("EXISTING QTY: " + existingQuantity);

            if (quantityDifference > 0 && product.getQuantity() < quantityDifference) {
                throw new InsufficientQuantityException("Requested quantity exceeds available quantity");
            }

            product.setQuantity(product.getQuantity() - quantityDifference);
            productRepository.save(product);

            OrderItem updatedItem = new OrderItem(product, newQuantity, product.getPrice().multiply(BigDecimal.valueOf(newQuantity)));
            order.getOrderItems().add(updatedItem);
            order.setTotalAmount(order.getTotalAmount().add(updatedItem.getPrice()));
        }
        orderRepository.save(order);
        itemsToRemove.forEach(orderItemRepository::deleteById);
        return convertToOrderResponse(order);
    }

    @Transactional
    @Override
    public int importOrdersFromCsv(MultipartFile file) {
        Map<Long, Order> orderMap = new HashMap<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] line;
            int count = 0;
            reader.readNext();

            while ((line = reader.readNext()) != null) {
                Long orderId = Long.parseLong(line[0]);
                LocalDate orderDate = LocalDate.parse(line[1], DateTimeFormatter.ofPattern("MM/dd/yyyy"));
                OrderStatus status = OrderStatus.valueOf(line[2]);
                Long productId = Long.parseLong(line[4]);
                BigDecimal price = new BigDecimal(line[7]);
                int quantity = Integer.parseInt(line[8]);

                Order order = orderMap.computeIfAbsent(
                        orderId,
                        id -> Order.builder()
                                .orderDate(orderDate)
                                .orderStatus(status)
                                .totalAmount(BigDecimal.ZERO)
                                .orderItems(new ArrayList<>())
                                .build()
                );

                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new NotFoundException("Product not found: " + productId));

                // Check for quantity in stock
                if (product.getQuantity() < quantity) {
                    throw new InsufficientQuantityException("Insufficient stock for product " + product.getProductName() + " for orderId: " + orderId);
                }

                product.setQuantity(product.getQuantity() - quantity);
                productRepository.save(product);

                OrderItem item = new OrderItem(product, quantity, price);
                order.getOrderItems().add(item);

                order.setTotalAmount(order.getTotalAmount().add(price.multiply(new BigDecimal(quantity))));
                count++;
            }
            orderRepository.saveAll(orderMap.values());
            return count;
        } catch (Exception e) {
            throw new CSVImportException("Error processing CSV file " + e.getMessage());
        }
    }

    @Override
    public byte[] exportOrdersToCsv() {
        List<Order> orders = orderRepository.findAll();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(outputStream))) {

            writer.writeNext(new String[]{String.format("As of %s: ", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a")))});
            writer.writeNext(new String[]{"Order ID", "Order Date", "Order Status", "Total Amount", "Product Id", "Product Name", "Description", "Price", "Quantity"});

            for (Order order : orders) {
                int index = 0;

                for (OrderItem orderItem : order.getOrderItems()) {
                    writer.writeNext(new String[]{
                            String.valueOf(order.getOrderId()),
                            String.valueOf(order.getOrderDate()),
                            String.valueOf(order.getOrderStatus()),
                            String.valueOf(order.getOrderItems().get(index).getPrice()),
                            String.valueOf(order.getOrderItems().get(index).getProduct().getProductId()),
                            String.valueOf(order.getOrderItems().get(index).getProduct().getProductName()),
                            String.valueOf(order.getOrderItems().get(index).getProduct().getDescription()),
                            String.valueOf(order.getOrderItems().get(index).getProduct().getPrice()),
                            String.valueOf(order.getOrderItems().get(index).getQuantity()),
                    });
                    index++;
                }
            }

            writer.flush();

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new CSVImportException("Error exporting orders to CSV file " + e.getMessage());
        }
    }

    @Override
    public void deleteOrderById(Long orderId) {
        if (!orderRepository.existsById(orderId))
            throw new NotFoundException("Order not found for ID: " + orderId);
        orderRepository.deleteById(orderId);
    }
}
