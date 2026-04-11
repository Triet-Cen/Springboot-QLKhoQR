package com.tttn.warehouseqr.modules.inventory.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class InventoryDashboardDto {
    private long totalProducts;
    private BigDecimal totalQuantity = BigDecimal.ZERO;
    private BigDecimal totalInventoryValue = BigDecimal.ZERO;
    private long lowStockWarnings;
}