package amnil.ims.service;

import amnil.ims.dto.request.SupplierRequest;
import amnil.ims.dto.response.SupplierResponse;
import amnil.ims.enums.Status;
import amnil.ims.exception.CSVExportException;
import amnil.ims.exception.NotFoundException;
import amnil.ims.model.Supplier;
import amnil.ims.repository.SupplierRepository;
import amnil.ims.service.supplier.SupplierService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class SupplierServiceTest {

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private SupplierService supplierService;

    //    Test 1: Save supplier
    @Test
    public void testSaveSupplier_Success() {
        SupplierRequest request = new SupplierRequest("Supplier A", "supA@gmail.com",
                "9800110011", Status.ACTIVE);
        Supplier supplier = Supplier.builder()
                .supplierId(1L)
                .supplierName("Supplier A")
                .email("supA@gmail.com")
                .phoneNumber("9800110011")
                .status(Status.ACTIVE)
                .build();

        Mockito.when(supplierRepository.save(Mockito.any(Supplier.class)))
                .thenReturn(supplier);
        SupplierResponse result = supplierService.saveSupplier(request);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getSupplierName(), supplier.getSupplierName());
        Assertions.assertEquals(result.getEmail(), supplier.getEmail());
        Assertions.assertEquals(result.getStatus(), supplier.getStatus());

        Mockito.verify(supplierRepository).save(Mockito.any(Supplier.class));
    }

    @Test
    public void testSaveSupplier_Failure() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            supplierService.saveSupplier(null);
        });

        Mockito.verify(supplierRepository, Mockito.never()).save(Mockito.any());
    }

    //    Test 2: Get Supplier By Name ID
    @Test
    public void testGetSupplierNameById_Success() {
        Supplier supplier = Supplier.builder().supplierName("Supplier A").build();
        Mockito.when(supplierRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(supplier));

        String supplierName = supplierService.getSupplierNameById(1L);

        Assertions.assertNotNull(supplierName);
        Assertions.assertEquals(supplierName, supplierService.getSupplierNameById(1L));

        Mockito.verify(supplierRepository, Mockito.atLeastOnce())
                .findById(Mockito.anyLong());
    }

    @Test
    public void testGetSupplierNameById_UnknownSupplier() {
        Supplier supplier = Supplier.builder().supplierName("Unknown supplier").build();

        Mockito.when(supplierRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(supplier));
        String supplierName = supplierService.getSupplierNameById(5L);

        Assertions.assertNotNull(supplierName);
        assert supplier != null;
        Assertions.assertEquals(supplier.getSupplierName(), supplierName);
    }

    //    Test 3: Get Suppliers
    @Test
    public void testGetSuppliers_Success() {
        Supplier supplier1 = Supplier.builder()
                .supplierId(1L)
                .supplierName("Supplier A")
                .email("supA@gmail.com")
                .phoneNumber("9800110011")
                .build();
        Supplier supplier2 = Supplier.builder()
                .supplierId(2L)
                .supplierName("Supplier B")
                .email("supB@gmail.com")
                .phoneNumber("9811001100")
                .build();
        List<Supplier> response = List.of(supplier1, supplier2);

        Mockito.when(supplierRepository.findAll())
                .thenReturn(response);
        List<SupplierResponse> result = supplierService.getSuppliers();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.size(), response.size());
        Assertions.assertEquals(result.get(0).getSupplierName(), response.get(0).getSupplierName());
        Assertions.assertEquals(result.get(0).getEmail(), response.get(0).getEmail());
        Assertions.assertEquals(result.get(0).getPhoneNumber(), response.get(0).getPhoneNumber());

        Mockito.verify(supplierRepository).findAll();
    }

    //    Test 4: Update Supplier Status
    @Test
    public void testUpdateSupplierStatus_Success() {
        Supplier oldSupplierDetails = Supplier.builder()
                .supplierId(1L)
                .supplierName("Supplier A")
                .status(Status.ACTIVE)
                .build();

        Mockito.when(supplierRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(oldSupplierDetails));
        Mockito.when(supplierRepository.save(Mockito.any(Supplier.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        SupplierResponse result = supplierService.updateSupplierStatus(1L, Status.INACTIVE);

        Assertions.assertNotNull(result);
        assert oldSupplierDetails != null;
        Assertions.assertEquals(result.getSupplierName(), oldSupplierDetails.getSupplierName());
        Assertions.assertEquals(result.getStatus(), Status.INACTIVE);

        Mockito.verify(supplierRepository).save(Mockito.any(Supplier.class));
    }

    @Test
    public void testUpdateSupplierStatus_Failure() {
        Long supplierId = 10L;
        Mockito.when(supplierRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () ->
                supplierService.updateSupplierStatus(supplierId, Status.INACTIVE)
        );

        Mockito.verify(supplierRepository, Mockito.never()).save(Mockito.any());
    }

    //    Test 5: Update Supplier
    @Test
    public void testUpdateSupplier_Success() {
        Long supplierId = 1L;
        Supplier existingSupplier = Supplier.builder()
                .supplierId(supplierId)
                .supplierName("Supplier A")
                .email("supA@gmail.com")
                .phoneNumber("9800110011")
                .status(Status.ACTIVE)
                .build();
        SupplierRequest request = SupplierRequest.builder()
                .supplierName("Supplier B")
                .email("supB@gmail.com")
                .phoneNumber("9800110011")
                .status(Status.INACTIVE)
                .build();

        Mockito.when(supplierRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(existingSupplier));
        Mockito.when(supplierRepository.save(Mockito.any(Supplier.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        SupplierResponse result = supplierService.updateSupplier(supplierId, request);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getSupplierName(), request.getSupplierName());
        Assertions.assertEquals(result.getEmail(), request.getEmail());
        Assertions.assertEquals(result.getStatus(), request.getStatus());

        Mockito.verify(supplierRepository).save(Mockito.any(Supplier.class));
    }

    @Test
    public void testUpdateSupplier_Failure() {
        Mockito.when(supplierRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () ->
                supplierService.updateSupplier(10L, SupplierRequest.builder().build())
        );

        Mockito.verify(supplierRepository, Mockito.never()).save(Mockito.any());
    }

    //    Test 6: Export Supplier
    @Test
    public void testExportSuppliers_Success() {
        List<Supplier> suppliers = Arrays.asList(
                new Supplier("ABC Suppliers", "abc@gmail.com", "1234567890", Status.ACTIVE),
                new Supplier("XYZ Suppliers", "xyz@gmail.com", "9876543210", Status.INACTIVE)
        );

        Mockito.when(supplierRepository.findAll()).thenReturn(suppliers);
        byte[] csvSupplierData = supplierService.exportToCsv();

        Assertions.assertNotNull(csvSupplierData);
        Assertions.assertTrue(csvSupplierData.length > 0);

        Mockito.verify(supplierRepository).findAll();
    }

    @Test
    public void testExportSuppliers_Failure() {
        Mockito.when(supplierRepository.findAll())
                .thenThrow(new CSVExportException("Problem exporting CSV"));

        CSVExportException exception = Assertions.assertThrows(
                CSVExportException.class,
                () -> supplierService.exportToCsv()
        );

        Assertions.assertEquals("Problem exporting CSV", exception.getMessage());

    }

    //    Test 7: Import Suppliers
    @Test
    public void testImportSuppliers_Success() {
        String csvContent = """
                Supplier Name,Email,Phone Number,Status
                ABC Suppliers,abc@gmail.com,1234567890,ACTIVE
                XYZ Suppliers,xyz@gmail.com,9876543210,INACTIVE
                """;
        MockMultipartFile file = new MockMultipartFile(
                "file", "suppliers.csv", "text/csv", csvContent.getBytes()
        );

        int importedSuppliersCount = supplierService.importSuppliersFromCsv(file);

        Assertions.assertEquals(2, importedSuppliersCount);
        Mockito.verify(supplierRepository, Mockito.times(importedSuppliersCount))
                .save(Mockito.any(Supplier.class));
    }

    @Test
    public void testImportSuppliers_Failure() {
        String supplierCsvContent = "";
        MockMultipartFile file = new MockMultipartFile(
                "file", "suppliers.csv", "text/csv", supplierCsvContent.getBytes()
        );

        int importedSuppliersCount = supplierService.importSuppliersFromCsv(file);

        Assertions.assertEquals(0, importedSuppliersCount);
        Mockito.verify(supplierRepository, Mockito.never()).save(Mockito.any(Supplier.class));
    }
}
