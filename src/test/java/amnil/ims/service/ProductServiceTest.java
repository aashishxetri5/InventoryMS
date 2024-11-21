package amnil.ims.service;

import amnil.ims.dto.request.ProductRequest;
import amnil.ims.dto.request.ProductRestockRequest;
import amnil.ims.dto.response.ProductResponse;
import amnil.ims.dto.response.ProductsBySupplierDto;
import amnil.ims.exception.CSVExportException;
import amnil.ims.exception.CSVImportException;
import amnil.ims.exception.NotFoundException;
import amnil.ims.model.Product;
import amnil.ims.model.Supplier;
import amnil.ims.repository.ProductRepository;
import amnil.ims.repository.SupplierRepository;
import amnil.ims.service.product.ProductService;
import amnil.ims.service.supplier.SupplierService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private SupplierService supplierService;

    @InjectMocks
    private ProductService productService;

    //    Test 1: saveProduct
    @Test
    public void testSaveProduct_Success() {
        ProductRequest request = ProductRequest.builder()
                .productName("Product A")
                .description("Product is good")
                .quantity(10)
                .price(BigDecimal.valueOf(1000.0))
                .supplier(Supplier.builder().supplierId(1L).build())
                .build();
        Product product = new Product(1L, "Product A", "Product is good",
                BigDecimal.valueOf(1000.0), 10, Supplier.builder().supplierId(1L).build());

        when(supplierService.existsById(anyLong())).thenReturn(true);
        when(productRepository.save(any(Product.class)))
                .thenReturn(product);
        var result = productService.saveProduct(request);

        assertNotNull(result);
        assertEquals(product.getProductName(), result.getProductName());
        assertEquals(product.getSupplier().getSupplierName(), result.getSupplierName());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    public void testSaveProduct_SupplierNotFound() {
        ProductRequest request = ProductRequest.builder()
                .productName("Product A")
                .description("Product is good")
                .quantity(10)
                .price(BigDecimal.valueOf(1000.0))
                .supplier(Supplier.builder().supplierId(1L).build())
                .build();

        assertThrows(NotFoundException.class, () -> {
            productService.saveProduct(request);
        });

        verify(productRepository, never()).save(any(Product.class));
    }


    //    Test 2: getProductsBySupplier
    @Test
    public void testGetProductsBySupplier_Success() {
        ProductsBySupplierDto firstProductBySupplier = ProductsBySupplierDto.builder()
                .productName("Product A")
                .price(BigDecimal.valueOf(1000.0))
                .supplierName("Supplier A")
                .build();
        ProductsBySupplierDto secondProductBySupplier = ProductsBySupplierDto.builder()
                .productName("Product B")
                .price(BigDecimal.valueOf(10000.0))
                .supplierName("Supplier A")
                .build();

        when(productRepository.findProductDetailsBySupplierId(anyLong()))
                .thenReturn(List.of(firstProductBySupplier, secondProductBySupplier));

        List<ProductsBySupplierDto> result = productService.getProductsBySupplier(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(firstProductBySupplier.getProductName(), result.get(0).getProductName());
        assertEquals(secondProductBySupplier.getProductName(), result.get(1).getProductName());

        verify(productRepository).findProductDetailsBySupplierId(anyLong());
    }

    @Test
    public void testGetProductsBySupplier_Empty() {
        when(productRepository.findProductDetailsBySupplierId(anyLong()))
                .thenReturn(List.of());

        List<ProductsBySupplierDto> result = productService.getProductsBySupplier(10L);

        assertEquals(0, result.size());
        assertTrue(result.isEmpty());
    }


    //    Test 3: getAllProducts
    @Test
    public void testGetAllProducts_Success() {
        Product product1 = Product.builder()
                .productId(1L)
                .productName("Product A")
                .description("Product A is good")
                .quantity(10)
                .price(BigDecimal.valueOf(1000.0))
                .supplier(Supplier.builder().supplierId(1L).build())
                .build();
        Product product2 = Product.builder()
                .productId(2L)
                .productName("Product B")
                .description("Product B is best")
                .quantity(100)
                .price(BigDecimal.valueOf(25000.0))
                .supplier(Supplier.builder().supplierId(1L).build())
                .build();

        when(productRepository.findAll())
                .thenReturn(List.of(product1, product2));

        List<ProductResponse> result = productService.getAllProducts();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(product1.getProductName(), result.get(0).getProductName());

        verify(productRepository).findAll();
    }

    @Test
    public void testGetAllProducts_Empty() {
        when(productRepository.findAll()).thenReturn(List.of());
        List<ProductResponse> result = productService.getAllProducts();

        assertEquals(0, result.size());
        assertTrue(result.isEmpty());
    }


    //    Test 4: getProductById
    @Test
    public void testGetProductById_Success() {
        Product product = Product.builder()
                .productId(1L)
                .productName("Product A")
                .description("Product A is good")
                .quantity(10)
                .price(BigDecimal.valueOf(1000.0))
                .supplier(Supplier.builder().supplierId(1L).build())
                .build();

        when(productRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(product));
        ProductResponse result = productService.getProductById(1L);

        assertNotNull(result);
        assert product != null;
        assertEquals(product.getProductName(), result.getProductName());
        assertEquals(product.getSupplier().getSupplierName(), result.getSupplierName());

        verify(productRepository).findById(anyLong());
    }

    @Test
    public void testGetProductById_NotFound() {
        when(productRepository.findById(anyLong()))
                .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> {
            productService.getProductById(2L);
        });
    }


    //    Test 5: deleteProductById
    @Test
    public void testDeleteProductById_Success() {
        when(productRepository.existsById(anyLong()))
                .thenReturn(Boolean.TRUE);

        productService.deleteProductById(1L);

        verify(productRepository).existsById(anyLong());
        verify(productRepository).deleteById(anyLong());
    }

    @Test
    public void testDeleteProductById_NotFound() {
        when(productRepository.existsById(anyLong()))
                .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> {
            productService.deleteProductById(2L);
        });

        verify(productRepository).existsById(anyLong());
        verify(productRepository, never()).deleteById(anyLong());
    }


    //    Test 6: updateProduct
    @Test
    public void testUpdateProduct_Success() {
        ProductRequest request = ProductRequest.builder()
                .productName("Product A")
                .description("Product A is good")
                .quantity(10)
                .price(BigDecimal.valueOf(1000.0))
                .supplier(Supplier.builder().supplierId(1L).build())
                .build();
        Product product = Product.builder()
                .productId(2L)
                .productName("Product B")
                .description("Product B is good")
                .quantity(10)
                .price(BigDecimal.valueOf(1000.0))
                .supplier(Supplier.builder().supplierId(1L).build())
                .build();

        when(productRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(product));

        ProductResponse result = productService.updateProduct(2L, request);

        assertNotNull(result);
        assert product != null;
        assertEquals(product.getProductName(), result.getProductName());
        assertEquals(product.getDescription(), result.getDescription());

        verify(productRepository).findById(anyLong());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    public void testUpdateProduct_NotFound() {
        when(productRepository.findById(anyLong()))
                .thenThrow(NotFoundException.class);

        assertThrows(NullPointerException.class, () -> {
            productService.updateProduct(1L, null);
        });
        assertThrows(NotFoundException.class, () -> {
            productService.updateProduct(1L, ProductRequest.builder().build());
        });

        verify(productRepository).findById(anyLong());
        verify(productRepository, never()).save(any(Product.class));
    }


    //    Test 7: importProductsFromCsv
    @Test
    public void testImportProductsFromCsv_Success() {
        String contents = """
                Name,Description,Price,Quantity,Supplier
                P1A,P1A is good,100.0,15,1
                P2A,P2A is better,150.0,8,1
                """;
        MockMultipartFile file = new MockMultipartFile(
                "file", "products.csv", "texts/csv", contents.getBytes()
        );

        when(supplierRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(Supplier.builder().supplierId(1L).build()));

        int importedProductsFromCsv = productService.importProductsFromCsv(file);
        assertEquals(2, importedProductsFromCsv);
        verify(supplierRepository, atLeastOnce()).findById(anyLong());
        verify(productRepository, atLeastOnce()).save(any(Product.class));
    }

    @Test
    public void testImportProductsFromCsv_NotFound() {
        String contents = """
                Name,Description,Price,Quantity,Supplier
                P1A,P1A is good,100.0,15,1
                P2A,P2A is better,150.0,8,1
                """;
        MockMultipartFile file = new MockMultipartFile(
                "file", "products.csv", "texts/csv", contents.getBytes()
        );

        when(supplierRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        CSVImportException exception = assertThrows(CSVImportException.class, () -> {
            productService.importProductsFromCsv(file);
        });

        assertTrue(exception.getMessage().contains("Failed to save!! Supplier " + 1L + " doesn't exist."));
        verify(supplierRepository, atMostOnce()).findById(anyLong());
        verify(productRepository, never()).save(any(Product.class));
    }


    //    Test 8: exportToCsv
    @Test
    public void testExportToCsv_Success() {
        List<Product> products = List.of(
                new Product(1L, "P1", "P1 Desc", BigDecimal.valueOf(100L), 10, Supplier.builder().supplierName("XYZ").build()),
                new Product(2L, "P2", "P2 Description", BigDecimal.valueOf(250L), 18, Supplier.builder().supplierName("ABC").build())
        );

        when(productRepository.findAll()).thenReturn(products);
        byte[] csvProductsData = productService.exportToCsv();

        assertNotNull(csvProductsData);
        assertTrue(csvProductsData.length > 0);

        verify(productRepository, times(1)).findAll();
    }

    @Test
    public void testExportToCsv_Failure() {
        Mockito.when(productRepository.findAll())
                .thenThrow(new CSVExportException("Problem exporting CSV"));

        CSVExportException exception = Assertions.assertThrows(
                CSVExportException.class,
                () -> productService.exportToCsv()
        );

        Assertions.assertEquals("Problem exporting CSV", exception.getMessage());
    }


    //    Test 9: restockProductIntoInventory
    @Test
    public void testRestockProductIntoInventory_Success() {
        ProductRestockRequest request = ProductRestockRequest.builder()
                .productId(1L)
                .quantity(20)
                .build();
        Product product = Product.builder()
                .productId(1L)
                .quantity(40)
                .supplier(Supplier.builder().supplierId(1L).supplierName("XYZ").build())
                .build();

        when(productRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(product));
        ProductResponse result = productService.restockProductIntoInventory(request);

        assertNotNull(result);
        assert product != null;
        assertEquals(product.getQuantity(), result.getQuantity());

        verify(productRepository, times(1)).findById(anyLong());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    public void testRestockProductIntoInventory_Failure() {
        ProductRestockRequest request = ProductRestockRequest.builder()
                .productId(1L)
                .quantity(20)
                .build();

        when(productRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class,
                () -> productService.restockProductIntoInventory(request)
        );

        verify(productRepository, times(1)).findById(anyLong());
        verify(productRepository, never()).save(any(Product.class));
    }


}
