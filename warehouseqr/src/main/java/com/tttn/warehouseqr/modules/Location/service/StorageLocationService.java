package com.tttn.warehouseqr.modules.Location.service;

import com.tttn.warehouseqr.modules.Location.dto.LocationInventoryScanDTO;
import com.tttn.warehouseqr.modules.Location.entity.StorageLocation;
import com.tttn.warehouseqr.modules.masterdata.product.dto.ProductScanDTO;

import java.util.List;
import java.util.Map;

public interface StorageLocationService {

    List<StorageLocation> findAll();

    List<StorageLocation> search(String keyword, Long warehouseId, String status);

    StorageLocation findById(Long id);

    StorageLocation save(StorageLocation location);

    StorageLocation update(Long id, StorageLocation location);

    List<ProductScanDTO> traceProductLocationsByQr(String qrContent);

    Map<String, Object> getLocationQrInfo(Long locationId);

    List<LocationInventoryScanDTO> traceInventoryByLocationQr(String qrContent);
}