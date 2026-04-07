package com.tttn.warehouseqr.modules.outbound.service.impl;

// 1. Link đến các file DTO của bạn

// 2. Link đến các file ĐÃ CÓ SẴN do team bạn tạo (Lưu ý: QrCodeResipotory bị sai chính tả chữ 's' và 'p')
import com.tttn.warehouseqr.modules.masterdata.product.entity.QrCode;
import com.tttn.warehouseqr.modules.masterdata.product.entity.ProductBatch;
import com.tttn.warehouseqr.modules.masterdata.product.repository.QrCodeResipotory;
import com.tttn.warehouseqr.modules.masterdata.product.repository.ProductBatchRepository;

// 3. Link đến các file MỚI (Thuộc module inventory và outbound mà bạn cần phải tự tạo)
import com.tttn.warehouseqr.modules.inventory.entity.InventoryLocationBalance;
import com.tttn.warehouseqr.modules.inventory.entity.InventoryHistory;
import com.tttn.warehouseqr.modules.inventory.repository.InventoryLocationBalanceRepository;
import com.tttn.warehouseqr.modules.inventory.repository.InventoryHistoryRepository;

import com.tttn.warehouseqr.modules.outbound.entity.OutboundReceiptItem;
import com.tttn.warehouseqr.modules.outbound.repository.OutboundReceiptItemRepository;

// 4. Các thư viện mặc định của Spring Boot và Java
import com.tttn.warehouseqr.modules.outbound.service.OutboundService;
import com.tttn.warehouseqr.modules.scan.dto.ScanSubmitDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
public class OutboundServiceImpl implements OutboundService {

    // Khai báo các Repository kết nối Database
    private final QrCodeResipotory qrCodeRepository; // Dùng đúng tên file sai chính tả của team bạn
    private final ProductBatchRepository batchRepository;
    private final OutboundReceiptItemRepository outboundItemRepository;
    private final InventoryLocationBalanceRepository balanceRepository;
    private final InventoryHistoryRepository historyRepository;

    // Constructor để Spring Boot tự động "bơm" vào
    public OutboundServiceImpl(QrCodeResipotory qrCodeRepository,
                               ProductBatchRepository batchRepository,
                               OutboundReceiptItemRepository outboundItemRepository,
                               InventoryLocationBalanceRepository balanceRepository,
                               InventoryHistoryRepository historyRepository) {
        this.qrCodeRepository = qrCodeRepository;
        this.batchRepository = batchRepository;
        this.outboundItemRepository = outboundItemRepository;
        this.balanceRepository = balanceRepository;
        this.historyRepository = historyRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public void processOutboundList(ScanSubmitDTO request, Long userId) {

        for (String qrContent : request.getQrContents()) {

            // 1. Giải mã QR
            QrCode qrCode = qrCodeRepository.findByQrContent(qrContent)
                    .orElseThrow(() -> new RuntimeException("Mã QR không hợp lệ: " + qrContent));

            Long batchId = qrCode.getReferenceId();
            ProductBatch batch = batchRepository.findById(batchId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Lô hàng!"));
            Long productId = batch.getProduct().getProduct_id();

            // 2. Kiểm tra Phiếu Xuất
            OutboundReceiptItem item = outboundItemRepository
                    .findByOutboundReceiptIdAndProductIdAndBatchId(request.getReceiptId(), productId, batchId)
                    .orElseThrow(() -> new RuntimeException("Sản phẩm quét không có trong phiếu xuất hiện tại!"));

            BigDecimal scanQty = BigDecimal.ONE;
            BigDecimal currentActual = item.getActualQty() != null ? item.getActualQty() : BigDecimal.ZERO;

            if (currentActual.add(scanQty).compareTo(item.getRequestedQty()) > 0) {
                throw new RuntimeException("Cảnh báo: Đã quét lố số lượng yêu cầu của phiếu xuất!");
            }

            // 3. Trừ Tồn Kho Thực Tế
            InventoryLocationBalance balance = balanceRepository
                    .findFirstByWarehouseIdAndProductIdAndBatchIdAndQtyGreaterThan(request.getWarehouseId(), productId, batchId, BigDecimal.ZERO)
                    .orElseThrow(() -> new RuntimeException("Lỗi: Hết hàng trong kho để xuất!"));

            if (balance.getQty().compareTo(scanQty) < 0) {
                throw new RuntimeException("Lỗi: Số lượng trên kệ không đủ!");
            }

            balance.setQty(balance.getQty().subtract(scanQty));
            balanceRepository.save(balance);

            // 4. Cập nhật số lượng đã nhặt vào phiếu xuất
            item.setActualQty(currentActual.add(scanQty));
            item.setPickedLocationId(balance.getLocationId());
            outboundItemRepository.save(item);

            // 5. Ghi lịch sử xuất kho
            InventoryHistory history = new InventoryHistory();
            history.setTransactionType("OUTBOUND");
            history.setFromLocationId(balance.getLocationId());
            history.setQtyChange(scanQty);
            history.setQrCodeId(qrCode.getQrCodeId());
            history.setBatchId(batchId);
            history.setProductId(productId);
            history.setWarehouseId(request.getWarehouseId());
            history.setUserId(userId);
            historyRepository.save(history);
        }
    }
}