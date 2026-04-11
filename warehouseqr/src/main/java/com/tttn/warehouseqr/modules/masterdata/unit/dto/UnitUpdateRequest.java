package com.tttn.warehouseqr.modules.masterdata.unit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UnitUpdateRequest {
    @NotNull(message = "ID_REQUIRED")
    private Long unitId;

    @NotBlank(message = "UNIT_NAME_REQUIRED")
    @Size(max = 100, message = "UNIT_NAME_TOO_LONG")
    private String unitName;

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }
}

