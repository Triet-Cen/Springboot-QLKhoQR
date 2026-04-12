package com.tttn.warehouseqr.modules.Location.repository;

import com.tttn.warehouseqr.modules.Location.entity.StorageLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface StorageLocationRepository extends JpaRepository<StorageLocation, Long> {

    List<StorageLocation> findByWarehouseId(Long warehouseId);

    List<StorageLocation> findByStatus(String status);

    List<StorageLocation> findByWarehouseIdAndStatus(Long warehouseId, String status);

    Optional<StorageLocation> findByLocationCode(String locationCode);

    @Query(value = """
        SELECT COALESCE(SUM(qty), 0)
        FROM inventory_location_balances
        WHERE location_id = :locationId
        """, nativeQuery = true)
    BigDecimal getUsedQtyByLocationId(@Param("locationId") Long locationId);
}