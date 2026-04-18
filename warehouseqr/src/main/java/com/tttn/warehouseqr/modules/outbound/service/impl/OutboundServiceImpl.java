package com.tttn.warehouseqr.modules.outbound.service.impl;

import com.tttn.warehouseqr.modules.masterdata.product.entity.QrCode;
import com.tttn.warehouseqr.modules.masterdata.product.entity.ProductBatch;
import com.tttn.warehouseqr.modules.masterdata.product.repository.QrCodeResipotory;
import com.tttn.warehouseqr.modules.masterdata.product.repository.ProductBatchRepository;
import com.tttn.warehouseqr.modules.masterdata.product.repository.ProductRepository;

import com.tttn.warehouseqr.modules.inventory.entity.InventoryLocationBalance;
import com.tttn.warehouseqr.modules.inventory.entity.InventoryHistory;
import com.tttn.warehouseqr.modules.inventory.repository.InventoryLocationBalanceRepository;
import com.tttn.warehouseqr.modules.inventory.repository.InventoryHistoryRepository;

import com.tttn.warehouseqr.modules.outbound.dto.OutboundItemDTO;
import com.tttn.warehouseqr.modules.outbound.dto.OutboundPickingSuggestionDTO;
import com.tttn.warehouseqr.modules.outbound.entity.OutboundReceipt;
import com.tttn.warehouseqr.modules.outbound.entity.OutboundIdempotencyRecord;
import com.tttn.warehouseqr.modules.outbound.entity.OutboundReceiptItem;
import com.tttn.warehouseqr.modules.outbound.repository.OutboundIdempotencyRepository;
import com.tttn.warehouseqr.modules.outbound.repository.OutboundReceiptItemRepository;
import com.tttn.warehouseqr.modules.outbound.repository.OutboundReceiptRepository;
import com.tttn.warehouseqr.modules.outbound.request.OutboundRequestDTO;
import com.tttn.warehouseqr.modules.outbound.service.OutboundService;
import com.tttn.warehouseqr.modules.salesorder.entity.SalesOrder;
import com.tttn.warehouseqr.modules.salesorder.entity.SalesOrderItem;
import com.tttn.warehouseqr.modules.salesorder.repository.SalesOrderItemRepository;
import com.tttn.warehouseqr.modules.salesorder.repository.SalesOrderRepository;
import com.tttn.warehouseqr.modules.scan.dto.ScanSubmitDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OutboundServiceImpl implements OutboundService {

    private static final String QR_REFERENCE_TYPE_BATCH = "BATCH";

    private final QrCodeResipotory qrCodeRepository;
    private final ProductBatchRepository batchRepository;
    private final ProductRepository productRepository;
    private final OutboundReceiptItemRepository outboundItemRepository;
    private final InventoryLocationBalanceRepository balanceRepository;
    private final InventoryHistoryRepository historyRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final SalesOrderItemRepository salesOrderItemRepository;
    private final OutboundReceiptRepository outboundReceiptRepository;
    private final OutboundIdempotencyRepository outboundIdempotencyRepository;

    public OutboundServiceImpl(QrCodeResipotory qrCodeRepository,
                               ProductBatchRepository batchRepository,
                               ProductRepository productRepository,
                               OutboundReceiptItemRepository outboundItemRepository,
                               InventoryLocationBalanceRepository balanceRepository,
                               InventoryHistoryRepository historyRepository,
                               SalesOrderRepository salesOrderRepository,
                               SalesOrderItemRepository salesOrderItemRepository,
                               OutboundReceiptRepository outboundReceiptRepository,
                               OutboundIdempotencyRepository outboundIdempotencyRepository) {
        this.qrCodeRepository = qrCodeRepository;
        this.batchRepository = batchRepository;
        this.productRepository = productRepository;
        this.outboundItemRepository = outboundItemRepository;
        this.balanceRepository = balanceRepository;
        this.historyRepository = historyRepository;
        this.salesOrderRepository = salesOrderRepository;
        this.salesOrderItemRepository = salesOrderItemRepository;
        this.outboundReceiptRepository = outboundReceiptRepository;
        this.outboundIdempotencyRepository = outboundIdempotencyRepository;
    }

    private String resolveProductName(Long productId) {
        return productRepository.findById(productId)
                .map(com.tttn.warehouseqr.modules.masterdata.product.entity.Product::getProductName)
                .orElse("SP " + productId);
    }

    private void validateNoShortage(OutboundRequestDTO request) {
        List<String> shortageMessages = new ArrayList<>();

        for (OutboundItemDTO itemDto : request.getItems()) {
            BigDecimal requestedQty = itemDto.getRequestedQty() != null
                    ? BigDecimal.valueOf(itemDto.getRequestedQty())
                    : BigDecimal.ZERO;
            BigDecimal actualQty = itemDto.getActualQty() != null
                    ? BigDecimal.valueOf(itemDto.getActualQty())
                    : BigDecimal.ZERO;
            BigDecimal qty = request.getSalesOrderId() != null
                    ? actualQty
                    : (actualQty.compareTo(BigDecimal.ZERO) > 0 ? actualQty : requestedQty);

            if (request.getSalesOrderId() != null && actualQty.compareTo(requestedQty) < 0) {
                shortageMessages.add(resolveProductName(itemDto.getProductId()) + " thiếu " + requestedQty.subtract(actualQty) + " so với yêu cầu.");
            }

            InventoryLocationBalance balance;
            if (itemDto.getLocationId() != null) {
                balance = balanceRepository.findByWarehouseIdAndLocationIdAndProductIdAndBatchId(
                        request.getWarehouseId(),
                        itemDto.getLocationId(),
                        itemDto.getProductId(),
                        itemDto.getBatchId()
                ).orElse(null);
            } else {
                balance = balanceRepository.findFirstByWarehouseIdAndProductIdAndBatchId(
                        request.getWarehouseId(),
                        itemDto.getProductId(),
                        itemDto.getBatchId()
                ).orElse(null);
            }

            BigDecimal availableQty = (balance != null && balance.getQty() != null) ? balance.getQty() : BigDecimal.ZERO;
            if (availableQty.compareTo(qty) < 0) {
                shortageMessages.add(resolveProductName(itemDto.getProductId()) + " tồn kho không đủ. Còn " + availableQty + ", cần " + qty + ".");
            }
        }

        if (!shortageMessages.isEmpty()) {
            throw new RuntimeException("Không thể xuất kho vì còn thiếu hàng:\n" + String.join("\n", shortageMessages));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processOutboundList(ScanSubmitDTO request, Long userId) {
        for (String qrContent : request.getQrContents()) {
            QrCode qrCode = qrCodeRepository.findByQrContentAndReferenceType(qrContent, QR_REFERENCE_TYPE_BATCH)
                    .orElseThrow(() -> {
                        boolean qrExistsWithOtherType = qrCodeRepository.findByQrContent(qrContent).isPresent();
                        String message = qrExistsWithOtherType
                                ? "QR này không phải QR lô hàng (BATCH), không thể dùng để trừ tồn kho xuất."
                                : "Mã QR không hợp lệ: " + qrContent;
                        return new RuntimeException(message);
                    });

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

        // 👉 LUẬT 1: CHỐT CHẶN BẢO MẬT TỪ QUẢN LÝ (Chưa duyệt -> Chặn lấy hàng)
        if ("DRAFT".equalsIgnoreCase(so.getStatus())) {
            throw new RuntimeException("LỖI BẢO MẬT: Đơn xuất này đang chờ Quản Lý Trưởng xác nhận. Thủ kho chưa được phép lấy hàng!");
        }

        List<OutboundPickingSuggestionDTO> suggestions = new ArrayList<>();

        for (SalesOrderItem item : so.getItems()) {
            BigDecimal orderedQty = item.getQuantity() != null ? item.getQuantity() : BigDecimal.ZERO;
            BigDecimal shippedQty = item.getShippedQty() != null ? item.getShippedQty() : BigDecimal.ZERO;
            BigDecimal remainingDemand = orderedQty.subtract(shippedQty);

            if (remainingDemand.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            OutboundPickingSuggestionDTO dto = new OutboundPickingSuggestionDTO();

            // 👉 MỚI BỔ SUNG: Truyền thông tin xuống Frontend để UI khóa nút
            dto.setSalesOrderId(so.getId());
            dto.setApprovalStatus(so.getStatus());
            dto.setPaymentStatus(so.getPaymentStatus());

            // 👉 THÊM DÒNG NÀY
            dto.setPaymentMethod(so.getPaymentMethod());

            dto.setProductId(item.getProductId());
            productRepository.findById(item.getProductId()).ifPresent(product -> {
                dto.setProductName(product.getProductName());
                dto.setSku(product.getSku());
            });
            // 👉 THÊM GIÁ BÁN TỪ SALES ORDER ITEM
            dto.setPrice(item.getUnitPrice());
            dto.setRequiredQty(remainingDemand);

            List<InventoryLocationBalanceRepository.PickingStockProjection> stocks =
                    balanceRepository.findAvailableStockForPicking(item.getProductId());

            List<OutboundPickingSuggestionDTO.LocationSuggestion> locations = new java.util.ArrayList<>();
            BigDecimal remaining = remainingDemand;
            int fifoRank = 1;

            for (InventoryLocationBalanceRepository.PickingStockProjection s : stocks) {
                if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
                    break;
                }

                BigDecimal availableQty = s.getAvailableQty() != null ? s.getAvailableQty() : BigDecimal.ZERO;
                if (availableQty.compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }

                BigDecimal suggestedPickQty = availableQty.min(remaining);
                OutboundPickingSuggestionDTO.LocationSuggestion loc = new OutboundPickingSuggestionDTO.LocationSuggestion();
                loc.setLocationId(s.getLocationId());
                loc.setLocationCode(s.getLocationCode());
                loc.setBatchId(s.getBatchId());
                loc.setLotCode(s.getLotCode());
                loc.setAvailableQty(availableQty);
                loc.setSuggestedPickQty(suggestedPickQty);
                loc.setFifoRank(fifoRank++);
                locations.add(loc);

                remaining = remaining.subtract(suggestedPickQty);
            }

            dto.setAllocatedQty(remainingDemand.subtract(remaining));
            dto.setShortageQty(remaining.max(BigDecimal.ZERO));

            dto.setSuggestedLocations(locations);
            suggestions.add(dto);
        }

        if (suggestions.isEmpty()) {
            throw new RuntimeException("Đơn hàng đã được xuất đủ, không còn số lượng cần lấy.");
        }

        return suggestions;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OutboundReceipt confirmOutbound(OutboundRequestDTO request, Long userId) {

        String idempotencyKey = request.getIdempotencyKey() != null ? request.getIdempotencyKey().trim() : "";
        boolean hasIdempotencyKey = !idempotencyKey.isEmpty();

        if (hasIdempotencyKey) {
            int claimed = outboundIdempotencyRepository.claimKey(idempotencyKey);

            if (claimed == 0) {
                OutboundIdempotencyRecord existing = outboundIdempotencyRepository.findByIdempotencyKey(idempotencyKey)
                        .orElseThrow(() -> new RuntimeException("Yêu cầu trùng đang được xử lý, vui lòng thử lại."));

                if ("COMPLETED".equalsIgnoreCase(existing.getStatus()) && existing.getOutboundReceiptId() != null) {
                    return outboundReceiptRepository.findById(existing.getOutboundReceiptId())
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu xuất từ yêu cầu trước đó."));
                }

                throw new RuntimeException("Yêu cầu trùng đang được xử lý, vui lòng thử lại sau.");
            }
        }

        SalesOrder order = null;
        Map<Long, SalesOrderItem> soItemByProduct = new HashMap<>();
        Map<Long, BigDecimal> requestQtyByProduct = new HashMap<>();

        // 👉 LUẬT 2: CHỐT CHẶN BẢO MẬT TỪ KẾ TOÁN (Chưa nộp tiền -> Chặn lưu kho)
        // 👉 CẬP NHẬT LUẬT 2: Chỉ khóa nếu chưa trả tiền VÀ không phải là đơn COD
        if (request.getSalesOrderId() != null) {
            order = salesOrderRepository.findById(request.getSalesOrderId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin đơn hàng!"));

            // Chỉ cho phép xuất khi kế toán đã xác nhận thanh toán.
            if (!"PAID".equalsIgnoreCase(order.getPaymentStatus())) {
                throw new RuntimeException("LỖI TÀI CHÍNH: Đơn hàng chưa thanh toán. Hệ thống khóa xuất kho!");
            }

            if (order.getItems() == null || order.getItems().isEmpty()) {
                throw new RuntimeException("Đơn hàng không có dòng sản phẩm để xuất kho!");
            }

            for (SalesOrderItem soItem : order.getItems()) {
                soItemByProduct.put(soItem.getProductId(), soItem);
            }
        }

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("Đơn hàng không có dòng sản phẩm để xuất kho!");
        }

        validateNoShortage(request);

        OutboundReceipt receipt = new OutboundReceipt();
        receipt.setOutboundReceiptCode("PX-" + System.currentTimeMillis());
        receipt.setWarehouseId(request.getWarehouseId());
        receipt.setCustomerId(request.getCustomerId() != null
                ? request.getCustomerId()
                : (order != null ? order.getCustomerId() : null));
        receipt.setCreatedBy(userId);
        receipt.setStatus("PENDING");
        receipt.setCreatedAt(LocalDateTime.now());
        receipt.setShippedAt(null);

        OutboundReceipt savedReceipt = outboundReceiptRepository.save(receipt);
        boolean hasAnyShipped = false;

        for (OutboundItemDTO itemDto : request.getItems()) {
            BigDecimal requestedQty = itemDto.getRequestedQty() != null
                    ? BigDecimal.valueOf(itemDto.getRequestedQty())
                    : BigDecimal.ZERO;
            BigDecimal actualQty = itemDto.getActualQty() != null
                    ? BigDecimal.valueOf(itemDto.getActualQty())
                    : BigDecimal.ZERO;
            // Với đơn có SO, chỉ xuất theo số lượng đã quét/thao tác thực tế.
            BigDecimal qty = request.getSalesOrderId() != null
                    ? actualQty
                    : (actualQty.compareTo(BigDecimal.ZERO) > 0 ? actualQty : requestedQty);

            if (qty.compareTo(BigDecimal.ZERO) <= 0) continue;

            if (order != null) {
                SalesOrderItem soItem = soItemByProduct.get(itemDto.getProductId());
                if (soItem == null) {
                    throw new RuntimeException("Sản phẩm ID " + itemDto.getProductId() + " không thuộc đơn hàng hiện tại!");
                }

                BigDecimal orderedQty = soItem.getQuantity() != null ? soItem.getQuantity() : BigDecimal.ZERO;
                BigDecimal shippedQty = soItem.getShippedQty() != null ? soItem.getShippedQty() : BigDecimal.ZERO;
                BigDecimal remainingQty = orderedQty.subtract(shippedQty);

                if (remainingQty.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new RuntimeException("Sản phẩm ID " + itemDto.getProductId() + " đã được xuất đủ trước đó, không thể xuất thêm!");
                }

                BigDecimal cumulatedRequestQty = requestQtyByProduct
                        .getOrDefault(itemDto.getProductId(), BigDecimal.ZERO)
                        .add(qty);

                if (cumulatedRequestQty.compareTo(remainingQty) > 0) {
                    throw new RuntimeException("Sản phẩm ID " + itemDto.getProductId() + " chỉ còn thiếu " + remainingQty + " để hoàn tất đơn, không thể xuất " + qty + "!");
                }

                requestQtyByProduct.put(itemDto.getProductId(), cumulatedRequestQty);
            }

            InventoryLocationBalance balance;
            if (itemDto.getLocationId() != null) {
                balance = balanceRepository.findByWarehouseIdAndLocationIdAndProductIdAndBatchId(
                        request.getWarehouseId(),
                        itemDto.getLocationId(),
                        itemDto.getProductId(),
                        itemDto.getBatchId()
                ).orElse(null);
            } else {
                balance = balanceRepository.findFirstByWarehouseIdAndProductIdAndBatchId(
                        request.getWarehouseId(),
                        itemDto.getProductId(),
                        itemDto.getBatchId()
                ).orElse(null);
            }

            BigDecimal availableQty = (balance != null && balance.getQty() != null) ? balance.getQty() : BigDecimal.ZERO;
            if (availableQty.compareTo(qty) < 0) {
                throw new RuntimeException(resolveProductName(itemDto.getProductId()) + " tồn kho không đủ. Còn " + availableQty + ", cần " + qty + ".");
            }
            BigDecimal shippedQty = availableQty.min(qty);

            if (shippedQty.compareTo(BigDecimal.ZERO) > 0) {
                hasAnyShipped = true;
            }

            if (balance != null && shippedQty.compareTo(BigDecimal.ZERO) > 0) {
                balance.setQty(availableQty.subtract(shippedQty));
                balanceRepository.save(balance);
            }

            if (request.getSalesOrderId() != null) {
                salesOrderItemRepository.updateShippedQty(request.getSalesOrderId(), itemDto.getProductId(), shippedQty);
            }

            OutboundReceiptItem receiptItem = new OutboundReceiptItem();
            receiptItem.setOutboundReceipt(savedReceipt);
            receiptItem.setProductId(itemDto.getProductId());
            receiptItem.setBatchId(itemDto.getBatchId());
            receiptItem.setPickedLocationId(balance != null ? balance.getLocationId() : itemDto.getLocationId());

            Double finalPrice = itemDto.getSellingPrice() != null ? itemDto.getSellingPrice() :
                    (itemDto.getPrice() != null ? itemDto.getPrice() : 0.0);
            receiptItem.setPrice(finalPrice);

            receiptItem.setRequestedQty(qty);

            receiptItem.setActualQty(shippedQty);

            outboundItemRepository.save(receiptItem);

            if (shippedQty.compareTo(BigDecimal.ZERO) > 0) {
                InventoryHistory history = new InventoryHistory();
                history.setTransactionType("OUTBOUND");
                history.setQtyChange(shippedQty.negate());
                history.setWarehouseId(request.getWarehouseId());
                history.setFromLocationId(balance != null ? balance.getLocationId() : itemDto.getLocationId());
                history.setBatchId(itemDto.getBatchId());
                history.setProductId(itemDto.getProductId());
                historyRepository.save(history);
            }
        }

        savedReceipt.setStatus(hasAnyShipped ? "SHIPPED" : "PENDING");
        savedReceipt.setShippedAt(hasAnyShipped ? LocalDateTime.now() : null);
        outboundReceiptRepository.save(savedReceipt);

        if (order != null) {
            List<SalesOrderItem> refreshedItems = salesOrderItemRepository.findBySalesOrderId(order.getId());
            boolean fullyShipped = true;

            for (SalesOrderItem soItem : refreshedItems) {
                BigDecimal orderedQty = soItem.getQuantity() != null ? soItem.getQuantity() : BigDecimal.ZERO;
                BigDecimal shippedQty = soItem.getShippedQty() != null ? soItem.getShippedQty() : BigDecimal.ZERO;

                if (shippedQty.compareTo(orderedQty) < 0) {
                    fullyShipped = false;
                    break;
                }
            }

            order.setStatus(fullyShipped ? "SHIPPED" : "PENDING");
            order.setUpdatedAt(LocalDateTime.now());
            salesOrderRepository.save(order);
        }

        if (hasIdempotencyKey) {
            outboundIdempotencyRepository.markCompleted(idempotencyKey, "COMPLETED", savedReceipt.getId());
        }

        return savedReceipt;
    }
}

