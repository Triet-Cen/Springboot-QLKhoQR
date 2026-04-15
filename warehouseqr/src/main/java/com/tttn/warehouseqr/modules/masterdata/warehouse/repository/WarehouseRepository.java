package com.tttn.warehouseqr.modules.masterdata.warehouse.repository;

import com.tttn.warehouseqr.modules.masterdata.warehouse.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    Optional<Warehouse> findByWarehouseCode(String code);
}

