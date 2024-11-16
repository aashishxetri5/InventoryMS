package amnil.ims.controller;

import amnil.ims.dto.request.SupplierRequest;
import amnil.ims.dto.response.ApiResponse;
import amnil.ims.dto.response.ProductsBySupplierDto;
import amnil.ims.dto.response.SupplierResponse;
import amnil.ims.enums.Status;
import amnil.ims.service.product.IProductService;
import amnil.ims.service.supplier.ISupplierService;
import amnil.ims.utils.FileResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/secure/supplier")
public class SupplierController {

    private final ISupplierService supplierService;
    private final IProductService productService;

    /**
     * Saves new supplier info
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping({"/new"})
    public ResponseEntity<?> saveSupplier(@Valid @RequestBody SupplierRequest request) {
        SupplierResponse response = supplierService.saveSupplier(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse("Product Added Successfully", response));
    }

    /**
     * Gets list of all the suppliers
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    @GetMapping({"", "/"})
    public ResponseEntity<?> getAllSuppliers() {
        List<SupplierResponse> response = supplierService.getSuppliers();
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse("Suppliers fetched successfully", response));
    }

    /**
     * Change status of a supplier from ACTIVE to INACTIVE and vice versa.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/changeStatus/{supplierId}")
    public ResponseEntity<?> Supplier(@PathVariable Long supplierId, @RequestParam Status newStatus) {
        SupplierResponse response = supplierService.updateSupplierStatus(supplierId, newStatus);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse("Supplier status changed successfully!", response));
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/update/{supplierId}")
    public ResponseEntity<?> updateSupplier(@PathVariable Long supplierId, @RequestBody SupplierRequest request) {
        SupplierResponse response = supplierService.updateSupplier(supplierId, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse("Updated successfully", response));
    }

    /**
     * @param supplierId ID of the supplier whose products needs to be fetched.
     * @return returns the list of Products By the supplier. If it doesn't exist, return Http status OK.
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    @GetMapping("/products")
    public ResponseEntity<?> productsBySupplier(@RequestParam Long supplierId) {
        List<ProductsBySupplierDto> productsBySupplierResponse = productService.getProductsBySupplier(supplierId);

        if (productsBySupplierResponse.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new ApiResponse(HttpStatus.NO_CONTENT.toString(), "No products found for the specified supplier."));
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse("Products fetched successfully", productsBySupplierResponse));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/export")
    public ResponseEntity<?> exportSupplier() {
        return FileResponseUtil.getResponseEntity(supplierService.exportToCsv(), "Suppliers.csv");
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    @PostMapping("/import")
    public ResponseEntity<?> importSupplier(@RequestParam("supplierCsv") MultipartFile file) {
        int numberOfImports = supplierService.importSuppliersFromCsv(file);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse("Success", "Successfully imported " + numberOfImports + " suppliers."));
    }
}
