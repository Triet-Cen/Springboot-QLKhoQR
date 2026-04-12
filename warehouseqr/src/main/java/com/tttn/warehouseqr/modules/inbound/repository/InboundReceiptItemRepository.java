package com.tttn.warehouseqr.modules.inbound.repository;

import com.tttn.warehouseqr.modules.inbound.entity.InboundReceiptItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InboundReceiptItemRepository extends JpaRepository<InboundReceiptItem,Long> {
    List<InboundReceiptItem> findByInboundReceiptId (Long receiptId);

    // ĐÃ FIX: Thêm i.importPrice (giá trị thứ 5) vào câu lệnh SELECT
    @Query("SELECT i.product.product_id, i.product.productName, i.actualQty, i.batch.lotCode, i.importPrice " +
            "FROM InboundReceiptItem i " +
            "WHERE i.inboundReceipt.id = :receiptId")
    List<Object[]> findItemsWithProductName(@Param("receiptId") Long receiptId);
}