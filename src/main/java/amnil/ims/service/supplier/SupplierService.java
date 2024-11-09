package amnil.ims.service.supplier;

import amnil.ims.dto.request.SupplierRequest;
import amnil.ims.dto.response.SupplierResponse;
import amnil.ims.enums.Status;
import amnil.ims.exception.CSVExportException;
import amnil.ims.exception.CSVImportException;
import amnil.ims.exception.NotFoundException;
import amnil.ims.model.Supplier;
import amnil.ims.repository.SupplierRepository;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierService implements ISupplierService {

    private final SupplierRepository supplierRepository;

    @Override
    public SupplierResponse saveSupplier(SupplierRequest request) {
        Supplier supplier = supplierRepository.save(convertToSupplier(request));

        return supplierResponseBuilder(supplier);
    }

    private Supplier convertToSupplier(SupplierRequest request) {
        return new Supplier(
                request.getSupplierName(),
                request.getEmail(),
                request.getPhoneNumber(),
                request.getStatus()
        );
    }

    private SupplierResponse supplierResponseBuilder(Supplier supplier) {
        return SupplierResponse.builder()
                .supplierId(supplier.getSupplierId())
                .supplierName(supplier.getSupplierName())
                .email(supplier.getEmail())
                .phoneNumber(supplier.getPhoneNumber())
                .status(supplier.getStatus())
                .build();
    }

    @Override
    public String getSupplierNameById(Long supplierId) {
        return supplierRepository.findById(supplierId)
                .map(Supplier::getSupplierName)
                .orElse("Unknown supplier");

//        () -> new NotFoundException("Uh oh!! Product was found but its supplier doesn't exist")
//        The above should be used with orElseThrow if a product cannot exist without its supplier.
    }

    @Override
    public boolean existsById(Long supplierId) {
        return supplierRepository.existsById(supplierId);
    }

    @Override
    public List<SupplierResponse> getSuppliers() {
        return supplierRepository.findAll().stream()
                .map(this::supplierResponseBuilder)
                .collect(Collectors.toList());
    }

    @Override
    public SupplierResponse updateSupplierStatus(Long supplierId, Status newStatus) {
        Optional<Supplier> supplierOptional = supplierRepository.findById(supplierId);

        return supplierOptional.map(
                        (supplier) -> {
                            supplier.setStatus(newStatus);
                            supplierRepository.save(supplier);
                            return supplierResponseBuilder(supplier);
                        })
                .orElseThrow(
                        () -> new NotFoundException("Supplier not found")
                );
    }

    @Override
    public SupplierResponse updateSupplier(Long supplierId, SupplierRequest request) {
        return supplierRepository.findById(supplierId)
                .map(
                        (supplier) -> {
                            supplier.setSupplierName(request.getSupplierName());
                            supplier.setEmail(request.getEmail());
                            supplier.setPhoneNumber(request.getPhoneNumber());
                            supplier.setStatus(request.getStatus());
                            supplierRepository.save(supplier);

                            return supplierResponseBuilder(supplier);
                        })
                .orElseThrow(
                        () -> new NotFoundException("Supplier doesn't exist")
                );
    }

    @Override
    public byte[] exportToCsv() {
        List<Supplier> supplierData = supplierRepository.findAll();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(outputStream))) {
            writer.writeNext(new String[]{String.format("As of %s: ", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a")))});
            writer.writeNext(new String[]{"ID", "Supplier Name", "Email", "Phone Number", "Status"});

            for (Supplier supplier : supplierData) {
                writer.writeNext(new String[]{
                        String.valueOf(supplier.getSupplierId()),
                        supplier.getSupplierName(),
                        supplier.getEmail(),
                        supplier.getPhoneNumber(),
                        String.valueOf(supplier.getStatus())
                });
            }
            writer.flush();

            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new CSVExportException("CSV could not be exported " + e.getMessage());
        }
    }

    @Override
    public int importSuppliersFromCsv(MultipartFile file) {
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] line;
            reader.readNext();
            int count = 0;

            while ((line = reader.readNext()) != null) {
                Supplier supplier = new Supplier();
                supplier.setSupplierName(line[0]);
                supplier.setEmail(line[1]);
                supplier.setPhoneNumber(line[2]);
                supplier.setStatus(Status.valueOf(line[3].toUpperCase()));

                supplierRepository.save(supplier);
                count++;
            }
            reader.close();
            return count;
        } catch (Exception e) {
            throw new CSVImportException("Error processing CSV file " + e.getMessage());
        }
    }


}
