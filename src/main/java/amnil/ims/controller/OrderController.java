package amnil.ims.controller;

import amnil.ims.dto.request.OrderRequest;
import amnil.ims.dto.response.ApiResponse;
import amnil.ims.dto.response.OrderResponse;
import amnil.ims.service.order.IOrderService;
import amnil.ims.utils.FileResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/secure/orders")
public class OrderController {

    private final IOrderService orderService;

    @PostMapping("/new")
    public ResponseEntity<?> placeNewOrder(@Valid @RequestBody OrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse("Order Placed Successfully", response));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllOrders() {
        List<OrderResponse> response = orderService.getAllOrders();
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse("Orders fetched successfully", response));
    }

    @PostMapping("/update/{orderId}")
    public ResponseEntity<?> updateOrder(@PathVariable("orderId") Long orderId, @Valid @RequestBody OrderRequest request) {
        OrderResponse response = orderService.updateOrder(orderId, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse("Order updated successfully", response));
    }

    @DeleteMapping("/delete/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable("orderId") Long orderId) {
        orderService.deleteOrderById(orderId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/import")
    public ResponseEntity<?> importOrders(@RequestParam("ordersCsv") MultipartFile file) {
        int numberOfImports = orderService.importOrdersFromCsv(file);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse("Success", "Successfully imported " + numberOfImports + " orders."));
    }

    @GetMapping("/export")
    public ResponseEntity<?> exportOrders() {
        return FileResponseUtil.getResponseEntity(orderService.exportOrdersToCsv(), "OrdersExport.csv");
    }

}
