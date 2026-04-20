package com.tttn.warehouseqr.modules.inbound.service;

import com.tttn.warehouseqr.modules.inbound.dto.InboundRequestDTO;
import com.tttn.warehouseqr.modules.inbound.entity.InboundReceipt;
import com.tttn.warehouseqr.modules.masterdata.product.dto.ProductScanDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface InboundService {
    // Xử lý tạo phiếu nhập và cập nhật kho
    InboundReceipt createInboundReceipt(InboundRequestDTO dto, Long userId);

    // Tìm kiếm thông tin phiếu nhập
    InboundReceipt getById(Long id);

    public List<ProductScanDTO> parseCsvToDTO(MultipartFile file);

    public void approveInboundReceipt(Long receiptId, String adminNote);

    public void rejectInboundReceipt(Long receiptId, String adminNote,String rejectAction);

    public List<InboundReceipt> getHistoryReceipts ();
}
