package com.tttn.warehouseqr.modules.inbound.service.impl;

import com.tttn.warehouseqr.modules.inbound.dto.InboundRequestDTO;
import com.tttn.warehouseqr.modules.inbound.entity.InboundReceipt;
import com.tttn.warehouseqr.modules.inbound.entity.InboundReceiptItem;
import com.tttn.warehouseqr.modules.inbound.repository.InboundReceiptItemRepository;
import com.tttn.warehouseqr.modules.inbound.repository.InboundReceiptRepository;
import com.tttn.warehouseqr.modules.inbound.service.InboundService;
import com.tttn.warehouseqr.modules.inventory.entity.InventoryHistory;
import com.tttn.warehouseqr.modules.inventory.repository.InventoryHistoryRepository;
import com.tttn.warehouseqr.modules.inventory.repository.InventoryLocationBalanceRepository;
import com.tttn.warehouseqr.modules.masterdata.product.repository.ProductBatchRepository;
import com.tttn.warehouseqr.modules.masterdata.product.repository.ProductRepository;
import com.tttn.warehouseqr.modules.purchase.repository.PurchaseOrderItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class InboundServiceImpl implements InboundService {
    private final InboundReceiptRepository receiptRepo;
    private final InboundReceiptItemRepository itemRepo;

    private final PurchaseOrderItemRepository poItemRepo;
    private final InventoryLocationBalanceRepository balanceRepo;
    private final InventoryHistoryRepository historyRepo;

    private final ProductRepository productRepo;
    private final ProductBatchRepository batchRepo;

    public InboundServiceImpl(InboundReceiptRepository receiptRepo, InboundReceiptItemRepository itemRepo, PurchaseOrderItemRepository poItemRepo, InventoryLocationBalanceRepository balanceRepo, InventoryHistoryRepository historyRepo, ProductRepository productRepo, ProductBatchRepository batchRepo) {
        this.receiptRepo = receiptRepo;
        this.itemRepo = itemRepo;
        this.poItemRepo = poItemRepo;
        this.balanceRepo = balanceRepo;
        this.historyRepo = historyRepo;
        this.productRepo = productRepo;
        this.batchRepo = batchRepo;
    }


    @Override
    @Transactional
    public InboundReceipt createInboundReceipt(InboundRequestDTO dto) {
        // 1. Kiểm tra Header
        if (dto.getPurchaseOrderId() == null) throw new RuntimeException("Lỗi: purchaseOrderId bị null");
        if (dto.getWarehouseId() == null) throw new RuntimeException("Lỗi: warehouseId bị null");

        InboundReceipt receipt = new InboundReceipt();
        receipt.setInboundReceiptCode(dto.getInboundReceiptCode() != null ? dto.getInboundReceiptCode() : "PN-" + System.currentTimeMillis());
        receipt.setPurchaseOrderId(dto.getPurchaseOrderId());
        receipt.setSupplierId(dto.getSupplierId());
        receipt.setWarehouseId(dto.getWarehouseId());
        receipt.setStatus("COMPLETED");
        receipt.setCreatedAt(LocalDateTime.now());
        receipt.setReceivedAt(LocalDateTime.now());

        InboundReceipt savedReceipt = receiptRepo.save(receipt);

        // 2. Duyệt Items
        for (var itemDto : dto.getItems()) {
            // KIỂM TRA TỪNG BIẾN TRƯỚC KHI GỌI REPO
            if (itemDto.getProductId() == null) throw new RuntimeException("Lỗi: productId của một item bị null");
            if (itemDto.getLocationId() == null) throw new RuntimeException("Lỗi: locationId bị null. Hãy kiểm tra lại dữ liệu gửi từ FE");

            InboundReceiptItem item = new InboundReceiptItem();
            item.setInboundReceipt(savedReceipt);

            var product = productRepo.findById(itemDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Không thấy SP: " + itemDto.getProductId()));
            item.setProduct(product);

            if (itemDto.getBatchId() != null) {
                var batch = batchRepo.findById(itemDto.getBatchId())
                        .orElseThrow(() -> new RuntimeException("Không thấy lô: " + itemDto.getBatchId()));
                item.setBatch(batch);
            }

            BigDecimal qty = BigDecimal.valueOf(itemDto.getActualQty() != null ? itemDto.getActualQty() : 0.0);
            item.setActualQty(qty);
            item.setExpectedQty(qty);
            item.setPutawayLocationId(itemDto.getLocationId());

            itemRepo.save(item);

            // Cập nhật các bảng liên quan - Đảm bảo các tham số KHÔNG NULL
            poItemRepo.updateReceivedQty(dto.getPurchaseOrderId(), itemDto.getProductId(), itemDto.getActualQty());

            // Cần cực kỳ cẩn thận với plusStock
            balanceRepo.plusStock(
                    dto.getWarehouseId(),
                    itemDto.getLocationId(),
                    itemDto.getProductId(),
                    itemDto.getBatchId(), // batchId có thể null nếu DB của bạn cho phép
                    itemDto.getActualQty()
            );

            InventoryHistory history = new InventoryHistory();
            history.setTransactionType("INBOUND");
            history.setProductId(itemDto.getProductId());
            history.setQtyChange(qty);
            history.setToLocationId(itemDto.getLocationId());
            history.setWarehouseId(dto.getWarehouseId());
            historyRepo.save(history);
        }
        return savedReceipt;
    }

    @Override
    public InboundReceipt getById(Long id) {
        return receiptRepo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu nhập"));
    }
}
