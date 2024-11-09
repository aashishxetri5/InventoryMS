package amnil.ims.service.product;

import amnil.ims.dto.request.ProductRequest;
import amnil.ims.dto.response.ProductResponse;
import amnil.ims.dto.response.ProductsBySupplierDto;
import amnil.ims.exception.CSVExportException;
import amnil.ims.exception.CSVImportException;
import amnil.ims.exception.NotFoundException;
import amnil.ims.model.Product;
import amnil.ims.model.Supplier;
import amnil.ims.repository.ProductRepository;
import amnil.ims.repository.SupplierRepository;
import amnil.ims.service.supplier.ISupplierService;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {

    private final ProductRepository productRepository;
    private final ISupplierService supplierService;
    private final SupplierRepository supplierRepository;

    @Override
    public ProductResponse saveProduct(ProductRequest request) {
        if (!supplierService.existsById(request.getSupplier().getSupplierId())) {
            throw new NotFoundException("Failed to save!! Supplier doesn't exist.");
        }

        Product product = productRepository.save(convertToProduct(request));

        return productResponseBuilder(product);
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

    private ProductResponse productResponseBuilder(Product product) {
        return ProductResponse.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .supplierName(supplierService.getSupplierNameById(product.getSupplier().getSupplierId()))
                .build();
    }

    @Override
    public List<ProductsBySupplierDto> getProductsBySupplier(Long supplierId) {
        return productRepository.findProductDetailsBySupplierId(supplierId);
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::productResponseBuilder)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse getProductById(Long productId) {
        return productRepository.findById(productId)
                .map(this::productResponseBuilder)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Product not found for %d", productId))
                );
    }

    @Override
    public void deleteProductById(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new NotFoundException("Failed to delete product!! Product doesn't exist.");
        }
        productRepository.deleteById(productId);
    }

    @Override
    public ProductResponse updateProduct(Long productId, ProductRequest request) {
        Product product = convertToProduct(request);
        return productRepository.findById(productId).map(
                        (existingProduct) -> {
                            existingProduct.setProductName(product.getProductName());
                            existingProduct.setDescription(product.getDescription());
                            existingProduct.setPrice(product.getPrice());
                            existingProduct.setQuantity(product.getQuantity());
                            existingProduct.setSupplier(product.getSupplier());
                            productRepository.save(existingProduct);

                            return productResponseBuilder(existingProduct);
                        })
                .orElseThrow(() -> new NotFoundException("Failed to update!! Product doesn't exist."));
    }

    @Override
    public int importProductsFromCsv(MultipartFile file) {
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] line;
            reader.readNext();
            int count = 0;

            while ((line = reader.readNext()) != null) {
                Product product = new Product();
                product.setProductName(line[0]);
                product.setDescription(line[1]);
                product.setPrice(new BigDecimal(line[2]));
                product.setQuantity(Integer.parseInt(line[3]));

                Long supplierId = Long.parseLong(line[4]);
                Supplier supplier = supplierRepository.findById(supplierId)
                        .orElseThrow(
                                () -> new NotFoundException("Failed to save!! Supplier " + supplierId + " doesn't exist.")
                        );
                product.setSupplier(supplier);

                productRepository.save(product);
                count++;
            }
            reader.close();
            return count;
        } catch (Exception e) {
            throw new CSVImportException("Error processing CSV file " + e.getMessage());
        }
    }

    @Override
    public byte[] exportToCsv() {
        List<Product> listOfProducts = productRepository.findAll();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(outputStream))) {
            writer.writeNext(new String[]{String.format("As of %s: ", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a")))});
            writer.writeNext(new String[]{"ID", "Product Name", "Description", "Price", "Quantity", "Supplier"});

            for (Product product : listOfProducts) {
                writer.writeNext(new String[]{
                        product.getProductId().toString(),
                        product.getProductName(),
                        product.getDescription(),
                        product.getPrice().toString(),
                        String.valueOf(product.getQuantity()),
                        product.getSupplier().getSupplierName()
                });
            }
            writer.flush();

            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new CSVExportException("There was a problem exporting CSV " + e.getMessage());
        }

    }
}
