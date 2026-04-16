package com.tttn.warehouseqr.modules.Location.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationInventoryScanDTO {
    private Long locationId;
    private String locationCode;
    private String aisleCode;
    private String rackCode;
    private String binCode;
    private String zoneName;

    private Long productId;
    private String productName;
    private String sku;
    private Long batchId;
    private String lotCode;

    private Double qty;
}