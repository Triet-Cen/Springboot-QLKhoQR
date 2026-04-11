package com.tttn.warehouseqr.modules.masterdata.warehouse.service;

import com.tttn.warehouseqr.modules.masterdata.warehouse.entity.Warehouse;
import com.tttn.warehouseqr.modules.masterdata.warehouse.repository.WarehouseRepository;
import com.tttn.warehouseqr.modules.masterdata.warehouse.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;

    @Override
    public List<Warehouse> findAll() {
        // Lấy danh sách toàn bộ kho để đổ ra dropdown
        return warehouseRepository.findAll();
    }
}