package amnil.ims.controller;

import amnil.ims.dto.request.SupplierRequest;
import amnil.ims.dto.response.ApiResponse;
import amnil.ims.dto.response.SupplierResponse;
import amnil.ims.enums.Status;
import amnil.ims.service.supplier.ISupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/secure/supplier")
public class SupplierController {

    private final ISupplierService supplierService;

    /**
     * Saves new supplier info
     *
     * @param request
     * @return
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping({"/new"})
    public ResponseEntity<?> saveSupplier(@RequestBody SupplierRequest request) {
        try {
            SupplierResponse response = supplierService.saveSupplier(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("Product Added Successfully", response));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Something went wrong", e.getMessage()));
        }
    }

    /**
     * Gets list of all the suppliers
     *
     * @return
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    @GetMapping({"", "/"})
    public ResponseEntity<?> getAllSuppliers() {
        try {
            List<SupplierResponse> response = supplierService.getSuppliers();

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse("Suppliers fetched successfully", response));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Something went wrong", e.getMessage()));
        }
    }

    /**
     * Change status of a supplier from ACTIVE to INACTIVE and vice versa.
     *
     * @param supplierId
     * @param newStatus
     * @return
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/changeStatus/{supplierId}")
    public ResponseEntity<?> Supplier(@PathVariable Long supplierId, @RequestParam Status newStatus) {
        try {
            SupplierResponse response = supplierService.updateSupplierStatus(supplierId, newStatus);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse("Supplier status changed successfully!", response));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Something went wrong", e.getMessage()));
        }
    }


    /**
     * @param supplierId
     * @param request
     * @return
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/update/{supplierId}")
    public ResponseEntity<?> updateSupplier(@PathVariable Long supplierId, @RequestBody SupplierRequest request) {
        try {
            SupplierResponse response = supplierService.updateSupplier(supplierId, request);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse("Updated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Something went wrong", e.getMessage()));
        }
    }
}
