
package com.tttn.warehouseqr.modules.stocktake.repository;

import com.tttn.warehouseqr.modules.stocktake.dto.StocktakeCompareDto;
import com.tttn.warehouseqr.modules.stocktake.entity.StocktakeItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StocktakeItemRepository extends JpaRepository<StocktakeItem, Long> {
    List<StocktakeItem> findBySessionId(Long sessionId);

    @Query("SELECT new com.tttn.warehouseqr.modules.stocktake.dto.StocktakeCompareDto(" +
            "p.sku, p.productName, wl.locationCode, " +
            "si.systemQty, si.actualQty, si.varianceQty, " +
            "CASE WHEN si.varianceQty != 0 THEN 'CHÊNH LỆCH' ELSE 'KHỚP' END) " +
            "FROM StocktakeItem si " +
            "JOIN Product p ON si.productId = p.product_id " +
            "LEFT JOIN WarehouseLocation wl ON si.locationId = wl.locationId " +
            "WHERE si.sessionId = :sessionId")
    List<StocktakeCompareDto> getCompareData(@Param("sessionId") Long sessionId);
}