package com.tttn.warehouseqr.modules.masterdata.supplier.service;

import com.tttn.warehouseqr.modules.masterdata.supplier.dto.SupplierCreateRequest;
import com.tttn.warehouseqr.modules.masterdata.supplier.dto.SupplierUpdateRequest;
import com.tttn.warehouseqr.modules.masterdata.supplier.entity.Supplier;

import java.util.List;

public interface SupplierService {
    void createSupplier(SupplierCreateRequest req);
    void updateSupplier(SupplierUpdateRequest req);
    SupplierUpdateRequest getUpdateById(Long id);
    List<Supplier> getAllSuppliers();
    void deleteSupplier(Long id);
    List<Supplier> searchSuppliers(String keyword);
}
