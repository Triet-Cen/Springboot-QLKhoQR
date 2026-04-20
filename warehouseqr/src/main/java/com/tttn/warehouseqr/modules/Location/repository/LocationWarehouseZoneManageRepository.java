package com.tttn.warehouseqr.modules.Location.repository;

import com.tttn.warehouseqr.modules.masterdata.warehouse.entity.WarehouseZone;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationWarehouseZoneManageRepository extends JpaRepository<WarehouseZone, Long> {

    interface ZoneEditView {
        Long getZoneId();
        String getZoneCode();
        String getZoneName();
        Long getWarehouseId();
    }

    @Query(value = """
        SELECT
            z.zone_id AS zoneId,
            z.zone_code AS zoneCode,
            z.zone_name AS zoneName,
            z.warehouse_id AS warehouseId
        FROM warehouse_zones z
        WHERE z.zone_id = :zoneId
        """, nativeQuery = true)
    ZoneEditView findZoneEditViewById(Long zoneId);

    @Query(value = """
        SELECT COUNT(*)
        FROM warehouse_zones
        WHERE zone_code = :zoneCode
        """, nativeQuery = true)
    long countByZoneCode(String zoneCode);

    @Query(value = """
        SELECT COUNT(*)
        FROM warehouse_zones
        WHERE zone_code = :zoneCode AND zone_id <> :zoneId
        """, nativeQuery = true)
    long countByZoneCodeAndZoneIdNot(String zoneCode, Long zoneId);

    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO warehouse_zones(zone_code, zone_name, warehouse_id, warehouses)
        VALUES (:zoneCode, :zoneName, :warehouseId, :warehouseId)
        """, nativeQuery = true)
    void insertZone(String zoneCode, String zoneName, Long warehouseId);

    @Modifying
    @Transactional
    @Query(value = """
        UPDATE warehouse_zones
        SET zone_code = :zoneCode,
            zone_name = :zoneName,
            warehouse_id = :warehouseId,
            warehouses = :warehouseId
        WHERE zone_id = :zoneId
        """, nativeQuery = true)
    void updateZone(Long zoneId, String zoneCode, String zoneName, Long warehouseId);
}