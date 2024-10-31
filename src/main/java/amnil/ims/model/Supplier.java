package amnil.ims.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
@Entity
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long supplierId;

    private String supplierName;

    private String contactInfo;

    @OneToMany(mappedBy = "supplier", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Product> productBySupplier;

    public Supplier(String supplierName, String contactInfo, List<Product> productBySupplier) {
        this.supplierName = supplierName;
        this.contactInfo = contactInfo;
        this.productBySupplier = productBySupplier;
    }
}
