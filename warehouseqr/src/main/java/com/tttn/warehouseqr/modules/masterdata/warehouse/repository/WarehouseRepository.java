package com.tttn.warehouseqr.modules.masterdata.warehouse.repository;

import com.tttn.warehouseqr.modules.masterdata.warehouse.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseRepository extends JpaRepository<Warehouse,Long> {

}
