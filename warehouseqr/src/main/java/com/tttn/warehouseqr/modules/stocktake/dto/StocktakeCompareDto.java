// StocktakeCompareDto.java
package com.tttn.warehouseqr.modules.stocktake.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class StocktakeCompareDto {
    private String sku;
    private String productName;
    private String locationCode;
    private BigDecimal systemQty;
    private BigDecimal actualQty;
    private BigDecimal varianceQty;
    private String status;
}