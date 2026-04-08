package com.tttn.warehouseqr.modules.purchase.service.impl;

import com.tttn.warehouseqr.modules.inbound.request.InboundItemRequestDTO;
import com.tttn.warehouseqr.modules.masterdata.product.repository.ProductBatchRepository;
import com.tttn.warehouseqr.modules.masterdata.product.repository.ProductRepository;
import com.tttn.warehouseqr.modules.purchase.entity.PurchaseOrderItem;
import com.tttn.warehouseqr.modules.purchase.repository.PurchaseOrderItemRepository;
import com.tttn.warehouseqr.modules.purchase.service.PurchaseOrderService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {
    private final PurchaseOrderItemRepository poItemRepo;
    private final ProductRepository productRepo;
    private final ProductBatchRepository productBatchRepo;
    public PurchaseOrderServiceImpl(PurchaseOrderItemRepository poItemRepo, ProductRepository productRepo, ProductBatchRepository productBatchRepo) {
        this.poItemRepo = poItemRepo;
        this.productRepo = productRepo;
        this.productBatchRepo = productBatchRepo;
    }

    @Override
    public List<InboundItemRequestDTO> getItemsByPoId(Long poId) {
        // 1. Tìm danh sách chi tiết PO
        List<PurchaseOrderItem> items = poItemRepo.findByPurchaseOrderId(poId);

        if (items == null || items.isEmpty()) {
            throw new RuntimeException("Đơn hàng #" + poId + " trống hoặc không tồn tại!");
        }

        return items.stream().map(item -> {
            InboundItemRequestDTO dto = new InboundItemRequestDTO();

            // Gán ID sản phẩm
            dto.setProductId(item.getProductId());

            // Xử lý số lượng an toàn
            dto.setActualQty(item.getOrderedQty() != null ? item.getOrderedQty().doubleValue() : 0.0);

            // Lấy tên sản phẩm an toàn (Tránh crash nếu ID sản phẩm không tồn tại trong bảng products)
            productRepo.findById(item.getProductId()).ifPresentOrElse(
                    p -> dto.setProductName(p.getProductName()),
                    () -> dto.setProductName("Sản phẩm không xác định (ID: " + item.getProductId() + ")")
            );

            dto.setBatchId(null);
            dto.setLocationId(1L);
            productBatchRepo.findById(item.getBatchId()).ifPresent(batch -> {
                dto.setLotCode(batch.getLotCode()); // Lấy "BATCH-2026-001" chẳng hạn
            });
            return dto;
        }).collect(Collectors.toList());
    }
}
