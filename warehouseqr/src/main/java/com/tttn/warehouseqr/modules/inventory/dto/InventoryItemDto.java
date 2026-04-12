package com.tttn.warehouseqr.modules.inventory.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InventoryItemDto {
    private String sku;
    private String productName;
    private String categoryName;
    private Double totalQuantity;
    private Double totalValue;
    private boolean lowStock;

    // Constructor thủ công cực kỳ quan trọng để khớp với Query JPQL
    public InventoryItemDto(String sku, String productName, String categoryName,
                            Object totalQuantity, Object totalValue, boolean lowStock) {
        this.sku = sku;
        this.productName = productName;
        this.categoryName = categoryName;
      
        this.totalQuantity = (totalQuantity != null) ? Double.valueOf(totalQuantity.toString()) : 0.0;
        this.totalValue = (totalValue != null) ? Double.valueOf(totalValue.toString()) : 0.0;
        this.lowStock = lowStock;
    }
}