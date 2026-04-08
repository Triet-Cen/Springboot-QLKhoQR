package com.tttn.warehouseqr.modules.scan.dto;

import lombok.Data;
import java.util.List;

@Data
public class ScanSubmitDTO {
    private Long receiptId;          // ID của Phiếu Xuất (Outbound Receipt) đang xử lý
    private Long warehouseId;        // Kho đang thực hiện xuất hàng
    private List<String> qrContents; // Mảng chứa các chuỗi QR đã quét trên màn hình
}