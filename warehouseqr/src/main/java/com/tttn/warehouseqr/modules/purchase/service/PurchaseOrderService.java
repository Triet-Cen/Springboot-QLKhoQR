package com.tttn.warehouseqr.modules.purchase.service;

import com.tttn.warehouseqr.modules.inbound.request.InboundItemRequestDTO;
import com.tttn.warehouseqr.modules.purchase.entity.PurchaseOrders;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public interface PurchaseOrderService {
    List<InboundItemRequestDTO> getItemsByPoId(Long poId);
    void createPoFromCsv(MultipartFile file, Long supplierId, Long warehouseId, Long userId);
    void createManualPurchaseOrder(Long supplierId,
                                   Long warehouseId,
                                   LocalDateTime expectedDeliveryDate,
                                   String notes,
                                   List<String> productIds,
                                   List<String> orderedQtys,
                                   List<String> unitPrices,
                                   List<String> batchIds,
                                   Long userId);
}
