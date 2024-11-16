package amnil.ims.service.order;

import amnil.ims.dto.request.OrderRequest;
import amnil.ims.dto.response.OrderResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IOrderService {
    OrderResponse createOrder(OrderRequest orderRequest);

    List<OrderResponse> getAllOrders();

    OrderResponse updateOrder(Long orderId, OrderRequest orderRequest);

    int importOrdersFromCsv(MultipartFile file);

    byte[] exportOrdersToCsv();

    void deleteOrderById(Long orderId);
}
