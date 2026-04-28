package com.tttn.warehouseqr.modules.masterdata.warehouse.services.imp;

import com.tttn.warehouseqr.modules.masterdata.warehouse.dto.WarehouseLocationDTO;
import com.tttn.warehouseqr.modules.masterdata.warehouse.entity.Warehouse;
import com.tttn.warehouseqr.modules.masterdata.warehouse.entity.WarehouseLocation;
import com.tttn.warehouseqr.modules.masterdata.warehouse.repository.WarehouseLocationRepository;
import com.tttn.warehouseqr.modules.masterdata.warehouse.repository.WarehouseRepository;
import com.tttn.warehouseqr.modules.masterdata.warehouse.services.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseLocationRepository locationRepository;

    @Override
    public List<Warehouse> findAll() {
        // Lấy danh sách toàn bộ kho để đổ ra dropdown
        return warehouseRepository.findAll();
    }

    @Override
    public List<Warehouse> getAllWarehouse() {
        return warehouseRepository.findAll();

    }

    @Override
    public List<WarehouseLocationDTO> getLocationsByWarehouseId(Long warehouseId) {
        List<WarehouseLocation> locations = locationRepository.findByWarehouses_WarehouseId(warehouseId);

        // Chuyển đổi Entity -> DTO
        return locations.stream()
                .map(loc -> new WarehouseLocationDTO(
                        loc.getLocationId(),
                        loc.getLocationCode()
                ))
                .collect(Collectors.toList());
    }
}