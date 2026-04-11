package com.tttn.warehouseqr.modules.masterdata.warehouse.repository;

import com.tttn.warehouseqr.modules.masterdata.warehouse.entity.WarehouseLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WarehouseLocationRepository extends JpaRepository<WarehouseLocation, Long> {
    Optional<WarehouseLocation> findByLocationCode (String locationCode);
}
