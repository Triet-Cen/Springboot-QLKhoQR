package com.tttn.warehouseqr.modules.Location.repository;

import com.tttn.warehouseqr.modules.masterdata.warehouse.entity.WarehouseZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarehouseZoneRepository extends JpaRepository<WarehouseZone, Long> {

    List<WarehouseZone> findByWarehouse_WarehouseId(Long warehouseId);
}