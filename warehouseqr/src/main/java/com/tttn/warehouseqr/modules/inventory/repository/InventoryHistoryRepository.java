package com.tttn.warehouseqr.modules.inventory.repository;

import com.tttn.warehouseqr.modules.inventory.entity.InventoryHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryHistoryRepository extends JpaRepository<InventoryHistory, Long> {
    @Query(value = "SELECT " +
            "  DATE_FORMAT(created_at, '%d/%m') as date_label, " +
            "  SUM(CASE WHEN transaction_type = 'INBOUND' THEN ABS(qty_change) ELSE 0 END) as inbound_qty, " +
            "  SUM(CASE WHEN transaction_type = 'OUTBOUND' THEN ABS(qty_change) ELSE 0 END) as outbound_qty " +
            "FROM inventory_history " +
            "WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
            "GROUP BY date_label " + // Nhóm theo alias đã định nghĩa ở SELECT
            "ORDER BY MIN(created_at) ASC", nativeQuery = true) // Sắp xếp theo thời gian thực
    List<Object[]> getInOutTrendLast7Days();
}