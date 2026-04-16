package com.tttn.warehouseqr.modules.Location.repository;

import com.tttn.warehouseqr.modules.Location.entity.StorageLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
    BigDecimal getUsedQtyByLocationId(Long locationId);

    interface ProductLocationView {
        Long getProductId();
        String getProductName();
        Long getBatchId();
        String getLotCode();
        String getSku();
        Long getLocationId();
        String getLocationCode();
        BigDecimal getQty();
    }

    @Query(value = """
        SELECT
            p.product_id AS productId,
            p.product_name AS productName,
            pb.batch_id AS batchId,
            pb.lot_code AS lotCode,
            p.sku AS sku,
            wl.location_id AS locationId,
            wl.location_code AS locationCode,
            ilb.qty AS qty
        FROM inventory_location_balances ilb
        JOIN warehouse_locations wl ON wl.location_id = ilb.location_id
        JOIN product_batches pb ON pb.batch_id = ilb.batch_id
        JOIN products p ON p.product_id = ilb.product_id
        WHERE ilb.batch_id = :batchId
          AND ilb.qty > 0
        ORDER BY ilb.qty DESC, wl.location_code ASC
        """, nativeQuery = true)
    List<ProductLocationView> findProductLocationsByBatchId(Long batchId);

    interface LocationInventoryView {
        Long getLocationId();
        String getLocationCode();
        String getAisleCode();
        String getRackCode();
        String getBinCode();
        String getZoneName();

        Long getProductId();
        String getProductName();
        String getSku();
        Long getBatchId();
        String getLotCode();

        BigDecimal getQty();
    }

    @Query(value = """
        SELECT
            wl.location_id AS locationId,
            wl.location_code AS locationCode,
            wl.aisle_code AS aisleCode,
            wl.rack_code AS rackCode,
            wl.bin_code AS binCode,
            wz.zone_name AS zoneName,

            p.product_id AS productId,
            p.product_name AS productName,
            p.sku AS sku,
            pb.batch_id AS batchId,
            pb.lot_code AS lotCode,
            ilb.qty AS qty
        FROM qr_codes q
        JOIN warehouse_locations wl
            ON wl.location_id = q.reference_id
           AND q.reference_type = 'LOCATION'
        LEFT JOIN warehouse_zones wz
            ON wz.zone_id = wl.zone_id
        LEFT JOIN inventory_location_balances ilb
            ON ilb.location_id = wl.location_id
        LEFT JOIN products p
            ON p.product_id = ilb.product_id
        LEFT JOIN product_batches pb
            ON pb.batch_id = ilb.batch_id
        WHERE q.qr_content = :qrContent
        ORDER BY p.product_name ASC, pb.lot_code ASC
        """, nativeQuery = true)
    List<LocationInventoryView> findInventoryByLocationQr(String qrContent);
}