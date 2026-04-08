package com.tttn.warehouseqr.modules.inbound.service;

import com.tttn.warehouseqr.modules.inbound.dto.InboundRequestDTO;
import com.tttn.warehouseqr.modules.inbound.entity.InboundReceipt;

public interface InboundService {
    // Xử lý tạo phiếu nhập và cập nhật kho
    InboundReceipt createInboundReceipt(InboundRequestDTO dto);

    // Tìm kiếm thông tin phiếu nhập
    InboundReceipt getById(Long id);
}
