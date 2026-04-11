package com.tttn.warehouseqr.modules.masterdata.supplier.service.implement;

import com.tttn.warehouseqr.modules.masterdata.supplier.dto.SupplierCreateRequest;
import com.tttn.warehouseqr.modules.masterdata.supplier.dto.SupplierUpdateRequest;
import com.tttn.warehouseqr.modules.masterdata.supplier.entity.Supplier;
import com.tttn.warehouseqr.modules.masterdata.supplier.repository.SupplierRepository;
import com.tttn.warehouseqr.modules.masterdata.supplier.service.SupplierService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SupplierServiceImpl implements SupplierService {
    private final SupplierRepository supplierRepository;

    public SupplierServiceImpl(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    private String normalize(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    @Override
    public void createSupplier(SupplierCreateRequest req) {
        String code = normalize(req.getSupplierCode());
        String name = normalize(req.getSupplierName());

        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Supplier code is required");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Supplier name is required");
        }
        if (supplierRepository.existsBySupplierCode(code)) {
            throw new IllegalArgumentException("Supplier code already exists");
        }

        Supplier supplier = new Supplier();
        supplier.setSupplierCode(code);
        supplier.setSupplierName(name);
        supplier.setContactPerson(normalize(req.getContactPerson()));
        supplier.setPhone(normalize(req.getPhone()));
        supplier.setEmail(normalize(req.getEmail()));
        supplier.setAddress(normalize(req.getAddress()));
        supplier.setActive(req.isActive());

        supplierRepository.save(supplier);
    }

    @Override
    public void updateSupplier(SupplierUpdateRequest req) {
        Supplier supplier = supplierRepository.findById(req.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier id not found"));

        String newCode = normalize(req.getSupplierCode());
        String newName = normalize(req.getSupplierName());

        if (newCode == null || newCode.isBlank()) {
            throw new RuntimeException("Supplier code is required");
        }
        if (newName == null || newName.isBlank()) {
            throw new RuntimeException("Supplier name is required");
        }

        boolean codeChanged = !newCode.equalsIgnoreCase(supplier.getSupplierCode());
        if (codeChanged && supplierRepository.existsBySupplierCode(newCode)) {
            throw new RuntimeException("Supplier code already exists");
        }

        supplier.setSupplierCode(newCode);
        supplier.setSupplierName(newName);
        supplier.setContactPerson(normalize(req.getContactPerson()));
        supplier.setPhone(normalize(req.getPhone()));
        supplier.setEmail(normalize(req.getEmail()));
        supplier.setAddress(normalize(req.getAddress()));
        supplier.setActive(req.isActive());

        supplierRepository.save(supplier);
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierUpdateRequest getUpdateById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier id not found"));

        SupplierUpdateRequest dto = new SupplierUpdateRequest();
        dto.setSupplierId(supplier.getSupplierId());
        dto.setSupplierCode(supplier.getSupplierCode());
        dto.setSupplierName(supplier.getSupplierName());
        dto.setContactPerson(supplier.getContactPerson());
        dto.setPhone(supplier.getPhone());
        dto.setEmail(supplier.getEmail());
        dto.setAddress(supplier.getAddress());
        dto.setActive(supplier.isActive());

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Supplier> searchSuppliers(String keyword) {
        String kw = normalize(keyword);
        return supplierRepository.searchByKeyWord(kw);
    }

    @Override
    public void deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier id not found"));
        supplierRepository.delete(supplier);
    }



}
