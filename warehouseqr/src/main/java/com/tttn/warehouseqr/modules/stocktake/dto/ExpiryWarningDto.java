
package com.tttn.warehouseqr.modules.stocktake.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpiryWarningDto {
    private String sku;
    private String productName;
    private String batchCode;
    private LocalDate expiryDate;

    private long daysRemaining;

    public ExpiryWarningDto(String sku, String productName, String batchCode, LocalDate expiryDate) {
        this.sku = sku;
        this.productName = productName;
        this.batchCode = batchCode;
        this.expiryDate = expiryDate;
        this.daysRemaining = 0; // giá trị tạm, sẽ được tính lại trong service
    }
}