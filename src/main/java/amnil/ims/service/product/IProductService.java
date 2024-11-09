package amnil.ims.service.product;

import amnil.ims.dto.request.ProductRequest;
import amnil.ims.dto.response.ProductResponse;
import amnil.ims.dto.response.ProductsBySupplierDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IProductService {
    ProductResponse saveProduct(ProductRequest request);

    List<ProductsBySupplierDto> getProductsBySupplier(Long supplierId);

    List<ProductResponse> getAllProducts();

    ProductResponse getProductById(Long productId);

    void deleteProductById(Long productId);

    ProductResponse updateProduct(Long productId, ProductRequest request);

    int importProductsFromCsv(MultipartFile file);

    byte[] exportToCsv();
}
