
package com.tttn.warehouseqr.modules.stocktake.repository;

import com.tttn.warehouseqr.modules.stocktake.dto.StocktakeCompareDto;
import com.tttn.warehouseqr.modules.stocktake.entity.StocktakeItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StocktakeItemRepository extends JpaRepository<StocktakeItem, Long> {
    List<StocktakeItem> findBySessionId(Long sessionId);

    Optional<StocktakeItem> findFirstBySessionIdAndProductIdAndBatchIdAndLocationId(
            Long sessionId, Long productId, Long batchId, Long locationId);

    @Query("SELECT si FROM StocktakeItem si WHERE si.sessionId = :sessionId " +
            "AND si.productId = :productId " +
            "AND (si.batchId = :batchId OR (si.batchId IS NULL AND :batchId IS NULL))")
    Optional<StocktakeItem> findStocktakeItemWithoutLocation(@Param("sessionId") Long sessionId,
                                                             @Param("productId") Long productId,
                                                             @Param("batchId") Long batchId);

    @Query("SELECT new com.tttn.warehouseqr.modules.stocktake.dto.StocktakeCompareDto(" +
            "p.sku, p.productName, wl.locationCode, " +
            "si.systemQty, si.actualQty, si.varianceQty, " +
            "CASE " +
            "   WHEN si.actualQty = 0 THEN 'CHƯA QUÉT' " +
            "   WHEN si.actualQty != si.systemQty THEN 'CHÊNH LỆCH' " +
            "   ELSE 'KHỚP' " +
            "END) " +
            "FROM StocktakeItem si " +
            "JOIN si.product p " +
            "LEFT JOIN si.location wl " +
            "WHERE si.sessionId = :sessionId")
    List<StocktakeCompareDto> getCompareData(@Param("sessionId") Long sessionId);
}