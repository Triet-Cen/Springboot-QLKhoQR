package com.tttn.warehouseqr.modules.masterdata.warehouse.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseLocationDTO {
    private long locationId;
    private String locationCode;
}
