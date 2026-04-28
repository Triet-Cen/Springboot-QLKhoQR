package com.tttn.warehouseqr.modules.transfer.services;

import com.tttn.warehouseqr.modules.transfer.dto.TransferItemDTO;
import com.tttn.warehouseqr.modules.transfer.dto.TransferRequestDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TransferOrderServices {

    public void processTransfer(TransferRequestDTO request, Long userId);
    public List<TransferItemDTO> parseTransferCsv(MultipartFile file);

    void approveTransferOrder(Long transferOrderId, Long approverId);
    void rejectTransferOrder(Long transferOrderId, String rejectReason, Long approverId);
}
