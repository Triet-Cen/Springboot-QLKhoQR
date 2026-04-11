package com.tttn.warehouseqr.modules.transfer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransferItemDTO {
    private Long productId;
    private Long batchId;
    private BigDecimal actualQty;
    private Long locationId;
    private Long targetLocationId;
    private Long toWarehouseId;
    private String productName;
    private String lotCode;
    private String locationCode;
 }
