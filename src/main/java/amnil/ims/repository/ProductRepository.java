package amnil.ims.repository;

import amnil.ims.dto.response.ProductsBySupplierDto;
import amnil.ims.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT new amnil.ims.dto.response.ProductsBySupplierDto(p.productName, p.price, p.supplier.supplierName ) " +
            "from Product p where p.supplier.supplierId = :supplierId")
    List<ProductsBySupplierDto> findProductDetailsBySupplierId(@Param("supplierId") Long supplierId);
}
