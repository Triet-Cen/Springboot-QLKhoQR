package com.tttn.warehouseqr.modules.masterdata.unit.service.impl;

import com.tttn.warehouseqr.modules.masterdata.unit.dto.UnitCreateRequest;
import com.tttn.warehouseqr.modules.masterdata.unit.dto.UnitUpdateRequest;
import com.tttn.warehouseqr.modules.masterdata.unit.entity.Unit;
import com.tttn.warehouseqr.modules.masterdata.unit.repository.UnitRepository;
import com.tttn.warehouseqr.modules.masterdata.unit.service.UnitService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UnitServiceImpl implements UnitService {
    private final UnitRepository unitRepository;

    public UnitServiceImpl(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    private String normalize(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Unit> getAllUnit(){
        return unitRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Unit> searchUnits(String keyword) {
        String kw = normalize(keyword);
        return unitRepository.searchByKeyword(kw);
    }

    @Override
    public void createUnit(UnitCreateRequest req){
        String unitName = normalize(req.getUnitName());
        if (unitName == null) {
            throw new RuntimeException("Tên đơn vị tính là bắt buộc");
        }
        if (unitRepository.existsByUnitNameIgnoreCase(unitName)) {
            throw new RuntimeException("Tên đơn vị tính đã tồn tại");
        }

        Unit unit = new Unit();
        unit.setUnitName(unitName);
        unitRepository.save(unit);
    }

    @Override
    @Transactional(readOnly = true)
    public UnitUpdateRequest getUpdateById(Long id) {
        Unit unit = unitRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Không tìm thấy đơn vị tính")
        );

        UnitUpdateRequest dto = new UnitUpdateRequest();
        dto.setUnitId(unit.getUnitId());
        dto.setUnitName(unit.getUnitName());
        return dto;
    }

    @Override
    public void updateUnit(UnitUpdateRequest req){
        Unit unit = unitRepository.findById(req.getUnitId()).orElseThrow(
                () -> new RuntimeException("Không tìm thấy đơn vị tính")
        );
        String newName = normalize(req.getUnitName());
        if (newName == null) {
            throw new RuntimeException("Tên đơn vị tính là bắt buộc");
        }

        boolean nameChanged = !newName.equalsIgnoreCase(unit.getUnitName());
        if (nameChanged && unitRepository.existsByUnitNameIgnoreCase(newName)) {
            throw new RuntimeException("Tên đơn vị tính đã tồn tại");
        }

        unit.setUnitName(newName);
        unitRepository.save(unit);
    }

    @Override
    public void deleteUnit(Long unitId){
        Unit unit = unitRepository.findById(unitId).orElseThrow(
                () -> new RuntimeException("Không tìm thấy đơn vị tính")
        );

        if (unit.getProducts() != null && !unit.getProducts().isEmpty()) {
            throw new RuntimeException("Đơn vị tính đang được sử dụng trong sản phẩm. Không thể xóa.");
        }

        unitRepository.delete(unit);
    }
}

