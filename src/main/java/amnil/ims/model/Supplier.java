package amnil.ims.model;

import amnil.ims.enums.Status;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@NoArgsConstructor
@Data
@Entity
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long supplierId;

    private String supplierName;

    private String email;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "supplier", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Product> productBySupplier;

    public Supplier(@NonNull String supplierName, @NonNull String email, @NonNull String phoneNumber, @NonNull Status status) {
        this.supplierName = supplierName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.status = status;
    }
}
