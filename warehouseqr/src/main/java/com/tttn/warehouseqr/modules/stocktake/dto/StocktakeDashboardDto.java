
package com.tttn.warehouseqr.modules.stocktake.dto;

import lombok.Data;

@Data
public class StocktakeDashboardDto {
    private long totalProducts;       // Tổng sản phẩm cần kiểm trong phiên
    private long totalScanned;        // Đã quét (số sản phẩm đã có actual_qty)
    private double completionPercent; // % hoàn thành
    private long varianceCount;       // Số sản phẩm có chênh lệch
}