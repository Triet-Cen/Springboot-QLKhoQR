package com.tttn.warehouseqr.modules.inventory.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class InventoryItemDto {
    private Long productId; // Thêm trường này để làm tính năng Drill-down
    private String sku;
    private String productName;
    private String categoryName;
    private BigDecimal totalQuantity;
    private BigDecimal totalValue;
    private boolean lowStock;

    // Constructor thủ công cực kỳ quan trọng để khớp với Query JPQL
    public InventoryItemDto(Long productId, String sku, String productName, String categoryName,
                            Object totalQuantity, Object totalValue, boolean lowStock) {
        this.productId = productId;
        this.sku = sku;
        this.productName = productName;
        this.categoryName = categoryName;

        // Xử lý an toàn: Ép kiểu từ Object sang String rồi parse ra BigDecimal
        this.totalQuantity = (totalQuantity != null) ? new BigDecimal(totalQuantity.toString()) : BigDecimal.ZERO;
        this.totalValue = (totalValue != null) ? new BigDecimal(totalValue.toString()) : BigDecimal.ZERO;
        this.lowStock = lowStock;
    }
}