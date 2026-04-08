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

import com.tttn.warehouseqr.modules.outbound.dto.OutboundItemDTO;
import com.tttn.warehouseqr.modules.outbound.dto.OutboundPickingSuggestionDTO;
import com.tttn.warehouseqr.modules.outbound.entity.OutboundReceipt;
import com.tttn.warehouseqr.modules.outbound.entity.OutboundReceiptItem;
import com.tttn.warehouseqr.modules.outbound.repository.OutboundReceiptItemRepository;

// 4. Các thư viện mặc định của Spring Boot và Java
import com.tttn.warehouseqr.modules.outbound.repository.OutboundReceiptRepository;
import com.tttn.warehouseqr.modules.outbound.request.OutboundRequestDTO;
import com.tttn.warehouseqr.modules.outbound.service.OutboundService;
import com.tttn.warehouseqr.modules.salesorder.entity.SalesOrder;
import com.tttn.warehouseqr.modules.salesorder.repository.SalesOrderItemRepository;
import com.tttn.warehouseqr.modules.salesorder.repository.SalesOrderRepository;
import com.tttn.warehouseqr.modules.scan.dto.ScanSubmitDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OutboundServiceImpl implements OutboundService {

    // Khai báo các Repository kết nối Database
    private final QrCodeResipotory qrCodeRepository; // Dùng đúng tên file sai chính tả của team bạn
    private final ProductBatchRepository batchRepository;
    private final OutboundReceiptItemRepository outboundItemRepository;
    private final InventoryLocationBalanceRepository balanceRepository;
    private final InventoryHistoryRepository historyRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final SalesOrderItemRepository salesOrderItemRepository;
    private final OutboundReceiptRepository outboundReceiptRepository;


    // Constructor để Spring Boot tự động "bơm" vào
    public OutboundServiceImpl(QrCodeResipotory qrCodeRepository,
                               ProductBatchRepository batchRepository,
                               OutboundReceiptItemRepository outboundItemRepository,
                               InventoryLocationBalanceRepository balanceRepository,
                               InventoryHistoryRepository historyRepository,
                               SalesOrderRepository salesOrderRepository, SalesOrderItemRepository salesOrderItemRepository,
                               OutboundReceiptRepository outboundReceiptRepository) {
        this.qrCodeRepository = qrCodeRepository;
        this.batchRepository = batchRepository;
        this.outboundItemRepository = outboundItemRepository;
        this.balanceRepository = balanceRepository;
        this.historyRepository = historyRepository;
        this.salesOrderRepository = salesOrderRepository;
        this.salesOrderItemRepository = salesOrderItemRepository;
        this.outboundReceiptRepository = outboundReceiptRepository;
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

    @Override
    public List<OutboundPickingSuggestionDTO> getPickingSuggestions(String soCode) {
        SalesOrder so = salesOrderRepository.findBySoCode(soCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng: " + soCode));

        return so.getItems().stream().map(item -> {
            OutboundPickingSuggestionDTO dto = new OutboundPickingSuggestionDTO();
            dto.setProductId(item.getProductId());
            dto.setRequiredQty(item.getQuantity());

            // Tìm sản phẩm trong kho, ưu tiên hàng nhập trước (FIFO)
            List<InventoryLocationBalance> stocks = balanceRepository.findAvailableStock(item.getProductId());

            // Chuyển đổi từ Entity Tồn kho sang danh sách gợi ý kệ gọn nhẹ
            List<OutboundPickingSuggestionDTO.LocationSuggestion> locations = stocks.stream().map(s -> {
                OutboundPickingSuggestionDTO.LocationSuggestion loc = new OutboundPickingSuggestionDTO.LocationSuggestion();
                loc.setLocationId(s.getLocationId());
                loc.setBatchId(s.getBatchId());
                loc.setAvailableQty(s.getQty());
                // Lưu ý: Bạn có thể cần join thêm bảng Location/Batch để lấy Code/LotCode hiển thị lên màn hình
                return loc;
            }).collect(Collectors.toList());

            dto.setSuggestedLocations(locations);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OutboundReceipt confirmOutbound(OutboundRequestDTO request, Long userId) {
        // 1. Lưu Header Phiếu xuất
        OutboundReceipt receipt = new OutboundReceipt();

        // Lưu ý: Sử dụng đúng tên hàm Setter trong Entity của bạn (outBoundReceiptCode)
        receipt.setOutboundReceiptCode("PX-" + System.currentTimeMillis());

        receipt.setWarehouseId(request.getWarehouseId()); // Sửa từ dto thành request
        receipt.setCustomerId(request.getCustomerId());
        receipt.setCreatedBy(userId); // Sử dụng tham số userId truyền vào
        receipt.setStatus("SHIPPED");
        receipt.setCreatedAt(LocalDateTime.now());
        receipt.setShippedAt(LocalDateTime.now());

        OutboundReceipt savedReceipt = outboundReceiptRepository.save(receipt);

        // 2. Duyệt danh sách hàng nhân sviên đã quét từ OutboundRequestDTO
        for (OutboundItemDTO itemDto : request.getItems()) {
            // Đổi Double sang BigDecimal để tính toán chính xác
            BigDecimal qty = BigDecimal.valueOf(itemDto.getActualQty());

            // A. Trừ kho nguyên tử (Atomic Update)
            int rowsAffected = balanceRepository.minusStock(
                    request.getWarehouseId(),
                    itemDto.getLocationId(),
                    itemDto.getProductId(),
                    itemDto.getBatchId(),
                    qty
            );

            // Nếu không có dòng nào bị ảnh hưởng -> Kho không đủ hàng hoặc sai thông tin
            if (rowsAffected == 0) {
                throw new RuntimeException("Sản phẩm ID " + itemDto.getProductId() +
                        " không đủ tồn kho tại vị trí kệ ID " + itemDto.getLocationId());
            }

            // B. Cập nhật số lượng thực tế đã xuất vào chi tiết Đơn hàng bán (Sales Order Item)
            salesOrderItemRepository.updateShippedQty(request.getSalesOrderId(), itemDto.getProductId(), qty);

            // C. Lưu chi tiết phiếu xuất (OutboundReceiptItem)
            OutboundReceiptItem receiptItem = new OutboundReceiptItem();
            receiptItem.setOutboundReceipt(savedReceipt);
            receiptItem.setProductId(itemDto.getProductId());
            receiptItem.setBatchId(itemDto.getBatchId());
            receiptItem.setPickedLocationId(itemDto.getLocationId());

            // Chuyển đổi Requested Qty từ DTO sang BigDecimal
            receiptItem.setRequestedQty(BigDecimal.valueOf(itemDto.getRequestedQty()));
            receiptItem.setActualQty(qty);

            outboundItemRepository.save(receiptItem);

            // D. Ghi nhật ký lịch sử kho (History) - Lưu số âm cho loại OUTBOUND
            InventoryHistory history = new InventoryHistory();
            history.setTransactionType("OUTBOUND");
            history.setQtyChange(qty.negate()); // Trừ kho
            history.setWarehouseId(request.getWarehouseId());
            history.setFromLocationId(itemDto.getLocationId());
            history.setBatchId(itemDto.getBatchId());
            history.setProductId(itemDto.getProductId());
            // history.setUserId(userId); // Nếu bảng history của bạn có cột user_id

            historyRepository.save(history);
        }

        return savedReceipt;
    }
}