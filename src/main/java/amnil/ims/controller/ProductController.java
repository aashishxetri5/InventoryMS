package amnil.ims.controller;

import amnil.ims.dto.request.ProductRequest;
import amnil.ims.dto.response.ApiResponse;
import amnil.ims.dto.response.ProductResponse;
import amnil.ims.service.product.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/secure/product")
@RequiredArgsConstructor
public class ProductController {

    private final IProductService productService;

    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    @PostMapping("/new")
    public ResponseEntity<?> addNewProduct(@RequestBody ProductRequest request) {
        try {
            ProductResponse response = productService.saveProduct(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("Product Added Successfully", response));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("INTERNAL SERVER ERROR", e.getMessage()));
        }
    }
}
