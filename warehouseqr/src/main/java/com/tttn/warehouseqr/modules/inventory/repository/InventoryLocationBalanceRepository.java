package com.tttn.warehouseqr.modules.inventory.repository;

import com.tttn.warehouseqr.modules.inventory.entity.InventoryLocationBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryLocationBalanceRepository extends JpaRepository<InventoryLocationBalance, Long> {
    java.util.Optional<InventoryLocationBalance> findFirstByWarehouseIdAndProductIdAndBatchId(Long warehouseId, Long productId, Long batchId);
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

    /**
     * Tìm tất cả vị trí kệ đang có sẵn sản phẩm này.
     * Ưu tiên sắp xếp theo update_at (Hàng nào vào trước xuất trước - FIFO)
     */
    @Query("SELECT b FROM InventoryLocationBalance b " +
            "WHERE b.productId = :productId AND b.qty > 0 " +
            "ORDER BY b.updateAt ASC")
    List<InventoryLocationBalance> findAvailableStock(@Param("productId") Long productId);

    /**
     * TRỪ KHO AN TOÀN (Atomic Update)
     * Chỉ trừ khi số lượng trong kho (qty) lớn hơn hoặc bằng số lượng cần xuất (:qty)
     * Trả về số dòng bị ảnh hưởng (nếu trả về 0 nghĩa là không đủ hàng để trừ)
     */
    @Modifying
    @Query("UPDATE InventoryLocationBalance b SET b.qty = b.qty - :qty, b.updateAt = NOW() " +
            "WHERE b.warehouseId = :wId " +
            "AND b.locationId = :lId " +
            "AND b.productId = :pId " +
            "AND b.batchId = :bId " +
            "AND b.qty >= :qty")
    int minusStock(@Param("wId") Long wId,
                   @Param("lId") Long lId,
                   @Param("pId") Long pId,
                   @Param("bId") Long bId,
                   @Param("qty") BigDecimal qty);

    @Query(value = "SELECT b.qty, l.location_code " +
            "FROM inventory_location_balances b " +
            "JOIN warehouse_locations l ON b.location_id = l.location_id " +
            "WHERE b.batch_id = :batchId", nativeQuery = true)
    List<Object[]> getStockAndLocationByBatchId(@Param("batchId") Long batchId);

    Optional<InventoryLocationBalance> findByBatchIdAndLocationId(Long batchId, Long locationId);
}