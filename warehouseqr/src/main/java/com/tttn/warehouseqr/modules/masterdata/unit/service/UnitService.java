package com.tttn.warehouseqr.modules.masterdata.unit.service;

import com.tttn.warehouseqr.modules.masterdata.unit.dto.UnitCreateRequest;
import com.tttn.warehouseqr.modules.masterdata.unit.dto.UnitUpdateRequest;
import com.tttn.warehouseqr.modules.masterdata.unit.entity.Unit;

import java.util.List;

public interface UnitService {
    void createUnit(UnitCreateRequest req);
    void updateUnit(UnitUpdateRequest req);
    UnitUpdateRequest getUpdateById(Long id);
    List<Unit> getAllUnit();
    List<Unit> searchUnits(String keyword);
    void deleteUnit(Long unitId);
}

