package com.tttn.warehouseqr.modules.salesorder.repository;

import com.tttn.warehouseqr.modules.salesorder.entity.SalesOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
@Repository
public interface SalesOrderItemRepository extends JpaRepository<SalesOrderItem, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE SalesOrderItem s SET s.shippedQty = COALESCE(s.shippedQty, 0) + :qty " +
            "WHERE s.salesOrder.id = :soId AND s.productId = :productId")
    int updateShippedQty(Long soId, Long productId, BigDecimal qty);

    List<SalesOrderItem> findBySalesOrderId(Long soId);
}
