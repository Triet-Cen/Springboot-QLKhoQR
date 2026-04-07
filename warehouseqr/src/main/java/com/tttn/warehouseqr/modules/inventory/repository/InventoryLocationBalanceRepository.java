package com.tttn.warehouseqr.modules.inventory.repository;

import com.tttn.warehouseqr.modules.inventory.entity.InventoryLocationBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface InventoryLocationBalanceRepository extends JpaRepository<InventoryLocationBalance, Long> {
    Optional<InventoryLocationBalance> findFirstByWarehouseIdAndProductIdAndBatchIdAndQtyGreaterThan(
            Long warehouseId, Long productId, Long batchId, BigDecimal qty);
}