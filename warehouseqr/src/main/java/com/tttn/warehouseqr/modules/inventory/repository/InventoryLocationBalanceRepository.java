package com.tttn.warehouseqr.modules.inventory.repository;

import com.tttn.warehouseqr.modules.inventory.entity.InventoryLocationBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface InventoryLocationBalanceRepository extends JpaRepository<InventoryLocationBalance, Long> {
    Optional<InventoryLocationBalance> findFirstByWarehouseIdAndProductIdAndBatchIdAndQtyGreaterThan(
            Long warehouseId, Long productId, Long batchId, BigDecimal qty);

    // Sử dụng tính năng "ON DUPLICATE KEY UPDATE" của MySQL để xử lý nhanh gọn
    @Modifying
    @Query(value = "INSERT INTO inventory_location_balances (warehouse_id, location_id, product_id, batch_id, qty, update_at) " +
            "VALUES (:wId, :lId, :pId, :bId, :qty, NOW()) " +
            "ON DUPLICATE KEY UPDATE qty = qty + :qty, update_at = NOW()",
            nativeQuery = true)
    void plusStock(@Param("wId") Long wId, @Param("lId") Long lId,
                   @Param("pId") Long pId, @Param("bId") Long bId,
                   @Param("qty") Double qty);
}