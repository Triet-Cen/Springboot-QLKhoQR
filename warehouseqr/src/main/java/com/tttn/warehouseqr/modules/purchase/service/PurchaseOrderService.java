package com.tttn.warehouseqr.modules.purchase.service;

import com.tttn.warehouseqr.modules.inbound.request.InboundItemRequestDTO;
import com.tttn.warehouseqr.modules.purchase.entity.PurchaseOrders;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PurchaseOrderService {
    List<InboundItemRequestDTO> getItemsByPoId(Long poId);
    PurchaseOrders createPoFromCsv(MultipartFile file, Long supplierId, Long warehouseId, Long userId);
}
