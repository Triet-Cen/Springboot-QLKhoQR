package com.tttn.warehouseqr.modules.outbound.repository;

import com.tttn.warehouseqr.modules.outbound.entity.OutboundReceiptItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OutboundReceiptItemRepository extends JpaRepository<OutboundReceiptItem, Long> {
    Optional<OutboundReceiptItem> findByOutboundReceiptIdAndProductIdAndBatchId(Long receiptId, Long productId, Long batchId);
}