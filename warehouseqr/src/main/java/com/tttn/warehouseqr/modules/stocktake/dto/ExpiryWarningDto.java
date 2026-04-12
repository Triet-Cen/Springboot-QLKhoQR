
package com.tttn.warehouseqr.modules.stocktake.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Data
@AllArgsConstructor
public class ExpiryWarningDto {
    private String sku;
    private String productName;
    private String batchCode;
    private LocalDate expiryDate;

    public long getDaysRemaining() {
        return ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
    }
}