package amnil.ims.service.supplier;

import amnil.ims.dto.request.SupplierRequest;
import amnil.ims.dto.response.SupplierResponse;
import amnil.ims.enums.Status;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ISupplierService {
    SupplierResponse saveSupplier(SupplierRequest request);

    String getSupplierNameById(Long supplierId);

    boolean existsById(Long supplierId);

    List<SupplierResponse> getSuppliers();

    SupplierResponse updateSupplierStatus(Long supplierId, Status status);

    SupplierResponse updateSupplier(Long supplierId, SupplierRequest request);

    byte[] exportToCsv();

    int importSuppliersFromCsv(MultipartFile file);
}
