package com.tttn.warehouseqr.modules.outbound.service;

import com.tttn.warehouseqr.modules.outbound.dto.OutboundPickingSuggestionDTO;
import com.tttn.warehouseqr.modules.outbound.entity.OutboundReceipt;
import com.tttn.warehouseqr.modules.outbound.request.OutboundRequestDTO;
import com.tttn.warehouseqr.modules.scan.dto.ScanSubmitDTO;

import java.util.List;

public interface OutboundService {
    public void processOutboundList(ScanSubmitDTO request, Long userId);


    // BƯỚC 1: Quét mã SO để lấy danh sách hàng và vị trí kệ gợi ý (MỚI)
    List<OutboundPickingSuggestionDTO> getPickingSuggestions(String soCode);

    // BƯỚC 2: Xử lý danh sách quét và xác nhận xuất kho (Dựa trên hàm cũ của bạn)
    OutboundReceipt confirmOutbound(OutboundRequestDTO request, Long userId);
}
