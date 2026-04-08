package com.tttn.warehouseqr.modules.purchase.service;

import com.tttn.warehouseqr.modules.inbound.request.InboundItemRequestDTO;

import java.util.List;

public interface PurchaseOrderService {
    List<InboundItemRequestDTO> getItemsByPoId(Long poId);
}
