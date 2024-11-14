package amnil.ims.controller;

import amnil.ims.dto.request.OrderRequest;
import amnil.ims.dto.response.ApiResponse;
import amnil.ims.dto.response.OrderResponse;
import amnil.ims.service.order.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/secure/orders")
public class OrderController {

    private final IOrderService orderService;

    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    @PostMapping("/new")
    public ResponseEntity<?> placeNewOrder(@RequestBody OrderRequest request) {
        try {

            OrderResponse response = orderService.createOrder(request);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("Order Placed Successfully", response));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Internal Server Error", e.getMessage()));
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    @GetMapping("/all")
    public ResponseEntity<?> getAllOrders() {
        try {
            List<OrderResponse> response = orderService.getAllOrders();

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse("Orders fetched successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Internal Server Error", e.getMessage()));
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    @PostMapping("/import")
    public ResponseEntity<?> importOrdersFromCsv(@RequestParam("ordersCsv") MultipartFile file) {
        try {

            int numberOfImports = orderService.importOrdersFromCsv(file);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse("Success", "Successfully imported " + numberOfImports + " products."));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Internal Server Error", e.getMessage()));
        }
    }

}
