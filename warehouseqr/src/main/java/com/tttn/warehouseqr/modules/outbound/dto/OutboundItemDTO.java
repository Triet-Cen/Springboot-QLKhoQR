package com.tttn.warehouseqr.modules.outbound.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OutboundItemDTO {
    private Long productId;
    private Long batchId;
    private Long locationId;
    private Double requestedQty;
    private Double actualQty;
    private Double sellingPrice;
}
