package amnil.ims.service.product;

import amnil.ims.dto.request.ProductRequest;
import amnil.ims.dto.response.ProductResponse;
import amnil.ims.exception.NotFoundException;
import amnil.ims.model.Product;
import amnil.ims.repository.ProductRepository;
import amnil.ims.service.supplier.ISupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {

    private final ProductRepository productRepository;
    private final ISupplierService supplierService;

    @Override
    public ProductResponse saveProduct(ProductRequest request) {
        if (!supplierService.existsById(request.getSupplier().getSupplierId())) {
            throw new NotFoundException("Failed to save!! Supplier doesn't exist.");
        }

        Product product = productRepository.save(convertToProduct(request));

        return ProductResponse.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .supplierName(supplierService.getSupplierNameById(product.getSupplier().getSupplierId()))
                .build();
    }

    private Product convertToProduct(ProductRequest request) {
        return new Product(
                request.getProductName(),
                request.getDescription(),
                request.getPrice(),
                request.getQuantity(),
                request.getSupplier()
        );
    }
}
