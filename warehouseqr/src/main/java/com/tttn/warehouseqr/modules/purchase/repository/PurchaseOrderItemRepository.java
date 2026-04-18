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
    @Modifying
    @Query("UPDATE PurchaseOrderItem p SET p.receivedQty = p.receivedQty + :qty " +
            "WHERE p.purchaseOrders.id = :poId AND p.product.product_id = :productId")
    void updateReceivedQty(@Param("poId") Long poId,
                           @Param("productId") Long productId,
                           @Param("qty") Double qty);
    List<PurchaseOrderItem> findByPurchaseOrders_Id(Long poId);

    @Query("SELECT i.product.product_id, i.product.productName, i.product.sku, i.orderedQty, i.unitPrice " +
            "FROM PurchaseOrderItem i WHERE i.purchaseOrders.id = :poId")
    List<Object[]> findItemsByPoIdForScan(@Param("poId") Long poId);

}
