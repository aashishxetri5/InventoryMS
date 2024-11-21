package amnil.ims.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    private String productName;

    private String description;

    private BigDecimal price;

    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    @ToString.Exclude
    private Supplier supplier;

    public Product(String productName, String description, BigDecimal price, int quantity, Supplier supplier) {
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.supplier = supplier;
    }
}
