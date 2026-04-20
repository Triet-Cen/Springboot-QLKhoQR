package com.tttn.warehouseqr.modules.Location.repository;

import com.tttn.warehouseqr.modules.masterdata.warehouse.entity.Warehouse;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationWarehouseRepository extends JpaRepository<Warehouse, Long> {

    boolean existsByWarehouseCode(String warehouseCode);

    boolean existsByWarehouseCodeAndWarehouseIdNot(String warehouseCode, Long warehouseId);

    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO warehouses(warehouse_code, warehouse_name, address, warehouse_address, status)
        VALUES (:warehouseCode, :warehouseName, :warehouseAddress, :warehouseAddress, 'ACTIVE')
        """, nativeQuery = true)
    void insertWarehouse(String warehouseCode, String warehouseName, String warehouseAddress);

    @Modifying
    @Transactional
    @Query(value = """
        UPDATE warehouses
        SET warehouse_code = :warehouseCode,
            warehouse_name = :warehouseName,
            address = :warehouseAddress,
            warehouse_address = :warehouseAddress
        WHERE warehouse_id = :warehouseId
        """, nativeQuery = true)
    void updateWarehouse(Long warehouseId, String warehouseCode, String warehouseName, String warehouseAddress);
}