package com.tttn.warehouseqr.modules.purchase.repository;

import com.tttn.warehouseqr.modules.purchase.entity.PurchaseOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem,Long> {
    @Modifying // Bắt buộc khi dùng câu lệnh UPDATE/DELETE
    @Query("UPDATE PurchaseOrderItem p SET p.receivedQty = p.receivedQty + :qty " +
            "WHERE p.purchaseOrderId = :poId AND p.productId = :productId")
    void updateReceivedQty(@Param("poId") Long poId, @Param("productId") Long productId, @Param("qty") Double qty);

    List<PurchaseOrderItem> findByPurchaseOrderId(Long poId);

}
