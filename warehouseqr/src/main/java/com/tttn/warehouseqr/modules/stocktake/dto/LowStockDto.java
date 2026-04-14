// LowStockDto.java (cho tồn kho thấp)
package com.tttn.warehouseqr.modules.stocktake.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class LowStockDto {
    private String sku;
    private String productName;
    private BigDecimal currentQty;
    private BigDecimal minStock;
}