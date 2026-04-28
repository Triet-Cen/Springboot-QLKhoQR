package com.tttn.warehouseqr.modules.inventory.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

@Data
public class InventoryDashboardDto {
    private long totalProducts;
    private BigDecimal totalQuantity = BigDecimal.ZERO;
    private BigDecimal totalInventoryValue = BigDecimal.ZERO;
    private long lowStockWarnings;

    /**
     * Getter tùy chỉnh cho Thymeleaf: dashboard.formattedTotalValue
     */
    public String getFormattedTotalValue() {
        return formatLargeNumber(this.totalInventoryValue);
    }

    /**
     * Getter tùy chỉnh cho Tổng số lượng (nếu số lượng cũng lên tới hàng triệu)
     */
    public String getFormattedTotalQuantity() {
        return formatLargeNumber(this.totalQuantity);
    }

    private String formatLargeNumber(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
            return "0";
        }

        BigDecimal ONE_BILLION = new BigDecimal("1000000000");
        BigDecimal ONE_MILLION = new BigDecimal("1000000");

        if (amount.compareTo(ONE_BILLION) >= 0) {
            // Lớn hơn hoặc bằng 1 Tỷ
            BigDecimal result = amount.divide(ONE_BILLION, 3, RoundingMode.HALF_UP).stripTrailingZeros();
            return result.toPlainString() + " Tỷ";

        } else if (amount.compareTo(ONE_MILLION) >= 0) {
            // Lớn hơn hoặc bằng 1 Triệu
            BigDecimal result = amount.divide(ONE_MILLION, 3, RoundingMode.HALF_UP).stripTrailingZeros();
            return result.toPlainString() + " Tr";

        } else {
            // Nhỏ hơn 1 triệu: Format phân cách hàng nghìn (VD: 500,000)
            DecimalFormat df = new DecimalFormat("#,###");
            return df.format(amount);
        }
    }
}