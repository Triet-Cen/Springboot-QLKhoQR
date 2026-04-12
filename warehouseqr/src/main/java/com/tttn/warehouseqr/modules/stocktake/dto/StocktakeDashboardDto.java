
package com.tttn.warehouseqr.modules.stocktake.dto;

import lombok.Data;

@Data
public class StocktakeDashboardDto {
    private long totalScanned;        // Đã quét
    private double completionPercent; // % hoàn thành
    private long varianceCount;       // Chênh lệch
    private double accuracy;          // Độ chính xác
    private double accuracyChange;    // Thay đổi so với kỳ trước (giả định +2.3%)
}