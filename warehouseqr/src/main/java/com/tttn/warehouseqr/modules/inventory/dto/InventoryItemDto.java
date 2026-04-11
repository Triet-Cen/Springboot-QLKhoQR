package com.tttn.warehouseqr.modules.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryItemDto {
    private String sku;
    private String productName;
    private String categoryName;
    private BigDecimal totalQuantity;
    private BigDecimal totalValue;
    private boolean isLowStock; //true = Báo động, false = Ổn định
}