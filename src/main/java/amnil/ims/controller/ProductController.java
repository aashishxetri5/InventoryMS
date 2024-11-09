package amnil.ims.controller;

import amnil.ims.dto.request.ProductRequest;
import amnil.ims.dto.response.ApiResponse;
import amnil.ims.dto.response.ProductResponse;
import amnil.ims.service.product.IProductService;
import amnil.ims.utils.FileResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    @GetMapping("/all")
    public ResponseEntity<?> getAllProducts() {
        try {
            List<ProductResponse> response = productService.getAllProducts();

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse("Fetched products successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Something went wrong", e.getMessage()));
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductById(@PathVariable Long productId) {
        try {

            ProductResponse response = productService.getProductById(productId);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse("Product fetched successfully", response));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Something went wrong", e.getMessage()));
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId) {
        try {
            productService.deleteProductById(productId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Something went wrong", e.getMessage()));
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    @PutMapping("/update/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable Long productId, @RequestBody ProductRequest request) {
        try {

            ProductResponse response = productService.updateProduct(productId, request);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse("Product fetched successfully", response));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("Something went wrong", e.getMessage()));
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    @PostMapping("/import")
    public ResponseEntity<?> importProductsFromCsv(@RequestParam("productCsv") MultipartFile file) {
        try {
            int numberOfImports = productService.importProductsFromCsv(file);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse("Success", "Successfully imported " + numberOfImports + " products."));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Something went wrong", e.getMessage()));
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/export")
    public ResponseEntity<?> exportProductToCsv() {
        return FileResponseUtil.getResponseEntity(productService.exportToCsv(), "Products.csv");
    }


}
