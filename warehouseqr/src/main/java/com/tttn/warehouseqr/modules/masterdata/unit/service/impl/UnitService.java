package com.tttn.warehouseqr.modules.masterdata.unit.service.impl;

import com.tttn.warehouseqr.modules.masterdata.unit.dto.UnitDTO;
import com.tttn.warehouseqr.modules.masterdata.unit.entity.Unit;
import com.tttn.warehouseqr.modules.masterdata.unit.repository.UnitRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnitService {
    private final UnitRepository unitRepository;

    public UnitService(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    public List<Unit> getAllUnit(){
        List<Unit> unit = unitRepository.findAll();

        return unit;
    }

    public Unit createUnit(UnitDTO unitDTO){
        Unit unit = new Unit();
        unit.setUnitName(unitDTO.getUnitName());

        return unitRepository.save(unit);
    }

    public  Unit updateUnit(long unitId, UnitDTO unitDTO){
        Unit unit = unitRepository.findById(unitId).orElseThrow(
                () -> new RuntimeException("Không tùm thấy Unit")
        );

        unit.setUnitName(unitDTO.getUnitName());

        return unitRepository.save(unit);
    }

    public void deleteUnit(long unitId){
        Unit unit = unitRepository.findById(unitId).orElseThrow(
                () -> new RuntimeException("Không tùm thấy Unit")
        );
        unitRepository.delete(unit);
    }
}
