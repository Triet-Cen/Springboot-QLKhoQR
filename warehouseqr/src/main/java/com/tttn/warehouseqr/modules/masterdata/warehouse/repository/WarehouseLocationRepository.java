package com.tttn.warehouseqr.modules.masterdata.warehouse.repository;

import com.tttn.warehouseqr.modules.masterdata.warehouse.entity.WarehouseLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseLocationRepository extends JpaRepository<WarehouseLocation, Long> {
    Optional<WarehouseLocation> findByLocationCode (String locationCode);

    List<WarehouseLocation> findByWarehouses_WarehouseId(Long warehouseId);
}
