package com.tttn.warehouseqr.modules.masterdata.warehouse.service;

import com.tttn.warehouseqr.modules.masterdata.warehouse.entity.Warehouse;

import java.util.List;

public interface WarehouseService {
    List<Warehouse> findAll();
}