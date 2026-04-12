package com.tttn.warehouseqr.modules.masterdata.warehouse.services;

import com.tttn.warehouseqr.modules.masterdata.warehouse.dto.WarehouseLocationDTO;
import com.tttn.warehouseqr.modules.masterdata.warehouse.entity.Warehouse;

import java.util.List;

public interface WarehouseService {
    List<Warehouse> findAll();

    List<Warehouse> getAllWarehouse();
    List<WarehouseLocationDTO> getLocationsByWarehouseId(Long warehouseId);

}