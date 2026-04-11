package com.tttn.warehouseqr.modules.outbound.repository;

import com.tttn.warehouseqr.modules.outbound.entity.OutboundReceiptItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List; // QUAN TRỌNG: Phải thêm import này
import java.util.Optional;

// SỬA: Đổi Integer thành Long cho đồng nhất với Entity
@Repository
public interface OutboundReceiptItemRepository extends JpaRepository<OutboundReceiptItem, Long> {

    // THÊM DÒNG NÀY: Để lấy danh sách hàng hóa khi nhấn nút "Xem" trên web
    List<OutboundReceiptItem> findByOutboundReceiptId(Long outboundReceiptId);

    // Giữ nguyên hàm cũ của bạn
    Optional<OutboundReceiptItem> findByOutboundReceiptIdAndProductIdAndBatchId(Long receiptId, Long productId, Long batchId);
}