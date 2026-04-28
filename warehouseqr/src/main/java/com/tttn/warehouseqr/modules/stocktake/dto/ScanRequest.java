package com.tttn.warehouseqr.modules.stocktake.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ScanRequest {
    private Long sessionId;

    // Bắt buộc quét đủ 2 mã
    private String locationQr; // Mã Vị trí (Kệ)
    private String productQr;  // Mã Sản phẩm/Lô

    private BigDecimal qty; // Số lượng đếm thực tế (Kiểm kê mù)

    // Cờ xác nhận: Bằng true nếu nhân viên đã bấm "Đồng ý" khi có cảnh báo chênh lệch
    private boolean confirmVariance;
}