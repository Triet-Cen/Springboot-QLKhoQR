package com.tttn.warehouseqr.modules.outbound.service.impl;

import com.tttn.warehouseqr.modules.masterdata.product.entity.QrCode;
import com.tttn.warehouseqr.modules.masterdata.product.entity.ProductBatch;
import com.tttn.warehouseqr.modules.masterdata.product.repository.QrCodeResipotory;
import com.tttn.warehouseqr.modules.masterdata.product.repository.ProductBatchRepository;

import com.tttn.warehouseqr.modules.inventory.entity.InventoryLocationBalance;
import com.tttn.warehouseqr.modules.inventory.entity.InventoryHistory;
import com.tttn.warehouseqr.modules.inventory.repository.InventoryLocationBalanceRepository;
import com.tttn.warehouseqr.modules.inventory.repository.InventoryHistoryRepository;

import com.tttn.warehouseqr.modules.outbound.dto.OutboundItemDTO;
import com.tttn.warehouseqr.modules.outbound.dto.OutboundPickingSuggestionDTO;
import com.tttn.warehouseqr.modules.outbound.entity.OutboundReceipt;
import com.tttn.warehouseqr.modules.outbound.entity.OutboundReceiptItem;
import com.tttn.warehouseqr.modules.outbound.repository.OutboundReceiptItemRepository;
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

    private final QrCodeResipotory qrCodeRepository;
    private final ProductBatchRepository batchRepository;
    private final OutboundReceiptItemRepository outboundItemRepository;
    private final InventoryLocationBalanceRepository balanceRepository;
    private final InventoryHistoryRepository historyRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final SalesOrderItemRepository salesOrderItemRepository;
    private final OutboundReceiptRepository outboundReceiptRepository;

    public OutboundServiceImpl(QrCodeResipotory qrCodeRepository,
                               ProductBatchRepository batchRepository,
                               OutboundReceiptItemRepository outboundItemRepository,
                               InventoryLocationBalanceRepository balanceRepository,
                               InventoryHistoryRepository historyRepository,
                               SalesOrderRepository salesOrderRepository,
                               SalesOrderItemRepository salesOrderItemRepository,
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processOutboundList(ScanSubmitDTO request, Long userId) {
        for (String qrContent : request.getQrContents()) {
            QrCode qrCode = qrCodeRepository.findByQrContent(qrContent)
                    .orElseThrow(() -> new RuntimeException("Mã QR không hợp lệ: " + qrContent));

            Long batchId = qrCode.getReferenceId();
            ProductBatch batch = batchRepository.findById(batchId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Lô hàng!"));
            Long productId = batch.getProduct().getProduct_id();

            OutboundReceiptItem item = outboundItemRepository
                    .findByOutboundReceiptIdAndProductIdAndBatchId(request.getReceiptId(), productId, batchId)
                    .orElseThrow(() -> new RuntimeException("Sản phẩm quét không có trong phiếu xuất hiện tại!"));

            BigDecimal scanQty = BigDecimal.ONE;
            BigDecimal currentActual = item.getActualQty() != null ? item.getActualQty() : BigDecimal.ZERO;

            if (currentActual.add(scanQty).compareTo(item.getRequestedQty()) > 0) {
                throw new RuntimeException("Cảnh báo: Đã quét lố số lượng yêu cầu của phiếu xuất!");
            }

            InventoryLocationBalance balance = balanceRepository
                    .findFirstByWarehouseIdAndProductIdAndBatchIdAndQtyGreaterThan(request.getWarehouseId(), productId, batchId, BigDecimal.ZERO)
                    .orElseThrow(() -> new RuntimeException("Lỗi: Hết hàng trong kho để xuất!"));

            if (balance.getQty().compareTo(scanQty) < 0) {
                throw new RuntimeException("Lỗi: Số lượng trên kệ không đủ!");
            }

            balance.setQty(balance.getQty().subtract(scanQty));
            balanceRepository.save(balance);

            item.setActualQty(currentActual.add(scanQty));
            item.setPickedLocationId(balance.getLocationId());
            outboundItemRepository.save(item);

            InventoryHistory history = new InventoryHistory();
            history.setTransactionType("OUTBOUND");
            history.setFromLocationId(balance.getLocationId());
            history.setQtyChange(scanQty.negate());
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

            List<InventoryLocationBalance> stocks = balanceRepository.findAvailableStock(item.getProductId());

            List<OutboundPickingSuggestionDTO.LocationSuggestion> locations = stocks.stream().map(s -> {
                OutboundPickingSuggestionDTO.LocationSuggestion loc = new OutboundPickingSuggestionDTO.LocationSuggestion();
                loc.setLocationId(s.getLocationId());
                loc.setBatchId(s.getBatchId());
                loc.setAvailableQty(s.getQty());
                return loc;
            }).collect(Collectors.toList());

            dto.setSuggestedLocations(locations);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OutboundReceipt confirmOutbound(OutboundRequestDTO request, Long userId) {
        OutboundReceipt receipt = new OutboundReceipt();
        receipt.setOutboundReceiptCode("PX-" + System.currentTimeMillis());
        receipt.setWarehouseId(request.getWarehouseId());
        receipt.setCustomerId(request.getCustomerId());
        receipt.setCreatedBy(userId);
        receipt.setStatus("SHIPPED");
        receipt.setCreatedAt(LocalDateTime.now());
        receipt.setShippedAt(LocalDateTime.now());

        OutboundReceipt savedReceipt = outboundReceiptRepository.save(receipt);

        for (OutboundItemDTO itemDto : request.getItems()) {
            BigDecimal qty = BigDecimal.valueOf(itemDto.getActualQty() != null ? itemDto.getActualQty() : 1.0);

            if (qty.compareTo(BigDecimal.ZERO) <= 0) continue;

            InventoryLocationBalance balance = balanceRepository.findFirstByWarehouseIdAndProductIdAndBatchId(
                    request.getWarehouseId(),
                    itemDto.getProductId(),
                    itemDto.getBatchId()
            ).orElseThrow(() -> new RuntimeException("Từ chối: Sản phẩm ID " + itemDto.getProductId() + " hoàn toàn KHÔNG CÓ trên kệ!"));

            if (balance.getQty().compareTo(qty) < 0) {
                throw new RuntimeException("TỪ CHỐI XUẤT KHO: Sản phẩm ID " + itemDto.getProductId() +
                        " chỉ còn " + balance.getQty() + " cái trên kệ. Không đủ để xuất " + qty + " cái!");
            }

            balance.setQty(balance.getQty().subtract(qty));
            balanceRepository.save(balance);

            if (request.getSalesOrderId() != null) {
                salesOrderItemRepository.updateShippedQty(request.getSalesOrderId(), itemDto.getProductId(), qty);
            }

            OutboundReceiptItem receiptItem = new OutboundReceiptItem();
            receiptItem.setOutboundReceipt(savedReceipt);
            receiptItem.setProductId(itemDto.getProductId());
            receiptItem.setBatchId(itemDto.getBatchId());
            receiptItem.setPickedLocationId(balance.getLocationId());

            // 🛠 SỬA LỖI TẠI ĐÂY: Ưu tiên lấy sellingPrice từ Web gửi xuống
            Double finalPrice = itemDto.getSellingPrice() != null ? itemDto.getSellingPrice() :
                    (itemDto.getPrice() != null ? itemDto.getPrice() : 0.0);
            receiptItem.setPrice(finalPrice);

            Double reqQty = itemDto.getRequestedQty();
            receiptItem.setRequestedQty(reqQty != null ? BigDecimal.valueOf(reqQty) : qty);

            receiptItem.setActualQty(qty);

            outboundItemRepository.save(receiptItem);

            InventoryHistory history = new InventoryHistory();
            history.setTransactionType("OUTBOUND");
            history.setQtyChange(qty.negate());
            history.setWarehouseId(request.getWarehouseId());
            history.setFromLocationId(balance.getLocationId());
            history.setBatchId(itemDto.getBatchId());
            history.setProductId(itemDto.getProductId());
            historyRepository.save(history);
        }

        return savedReceipt;
    }
}