package com.tttn.warehouseqr.modules.Location.service.Impl;

import com.tttn.warehouseqr.modules.Location.entity.StorageLocation;
import com.tttn.warehouseqr.modules.Location.repository.StorageLocationRepository;
import com.tttn.warehouseqr.modules.Location.service.StorageLocationService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StorageLocationServiceImpl implements StorageLocationService {

    private final StorageLocationRepository storageLocationRepository;

    public StorageLocationServiceImpl(StorageLocationRepository storageLocationRepository) {
        this.storageLocationRepository = storageLocationRepository;
    }

    @Override
    public List<StorageLocation> findAll() {
        List<StorageLocation> locations = storageLocationRepository.findAll();
        locations.forEach(this::syncUsageAndStatusFromInventory);
        return locations;
    }

    @Override
    public List<StorageLocation> search(String keyword, Long warehouseId, String status) {
        List<StorageLocation> locations = storageLocationRepository.findAll();

        locations.forEach(this::syncUsageAndStatusFromInventory);

        return locations.stream()
                .filter(loc -> warehouseId == null || warehouseId.equals(loc.getWarehouseId()))
                .filter(loc -> status == null || status.isBlank()
                        || (loc.getStatus() != null && loc.getStatus().equalsIgnoreCase(status)))
                .filter(loc -> {
                    if (keyword == null || keyword.isBlank()) return true;

                    String text = (
                            safe(loc.getLocationCode()) + " " +
                                    safe(loc.getAisleCode()) + " " +
                                    safe(loc.getRackCode()) + " " +
                                    safe(loc.getBinCode())
                    ).toLowerCase();

                    return text.contains(keyword.toLowerCase());
                })
                .collect(Collectors.toList());
    }

    @Override
    public StorageLocation findById(Long id) {
        StorageLocation location = storageLocationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vị trí kho với id = " + id));

        syncUsageAndStatusFromInventory(location);
        return location;
    }

    @Override
    public StorageLocation save(StorageLocation location) {
        if (location.getCapacity() == null) {
            location.setCapacity(0);
        }

        // vị trí mới chưa có tồn thì mặc định used = 0
        location.setUsedCapacity(0);

        // nếu user chọn INACTIVE thì giữ, còn lại để hệ thống tự tính
        if (!"INACTIVE".equalsIgnoreCase(location.getStatus())) {
            location.setStatus("EMPTY");
        }

        return storageLocationRepository.save(location);
    }

    @Override
    public StorageLocation update(Long id, StorageLocation location) {
        StorageLocation oldLocation = storageLocationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vị trí kho với id = " + id));

        oldLocation.setWarehouseId(location.getWarehouseId());
        oldLocation.setZoneId(location.getZoneId());
        oldLocation.setLocationCode(location.getLocationCode());
        oldLocation.setAisleCode(location.getAisleCode());
        oldLocation.setRackCode(location.getRackCode());
        oldLocation.setBinCode(location.getBinCode());
        oldLocation.setCapacity(location.getCapacity());
        oldLocation.setQrCodeId(location.getQrCodeId());
        oldLocation.setDescription(location.getDescription());

        // Không lấy usedCapacity từ form vì form không có field này
        BigDecimal usedQty = storageLocationRepository.getUsedQtyByLocationId(id);
        oldLocation.setUsedCapacity(usedQty != null ? usedQty.intValue() : 0);

        // Cho phép giữ INACTIVE nếu user chọn khóa vị trí
        if ("INACTIVE".equalsIgnoreCase(location.getStatus())) {
            oldLocation.setStatus("INACTIVE");
        } else {
            recalculateOperationalStatus(oldLocation);
        }

        return storageLocationRepository.save(oldLocation);
    }

    private void syncUsageAndStatusFromInventory(StorageLocation loc) {
        if (loc.getLocationId() == null) {
            loc.setUsedCapacity(0);
            if (!"INACTIVE".equalsIgnoreCase(loc.getStatus())) {
                loc.setStatus("EMPTY");
            }
            return;
        }

        BigDecimal usedQty = storageLocationRepository.getUsedQtyByLocationId(loc.getLocationId());
        loc.setUsedCapacity(usedQty != null ? usedQty.intValue() : 0);

        if (!"INACTIVE".equalsIgnoreCase(loc.getStatus())) {
            recalculateOperationalStatus(loc);
        }
    }

    private void recalculateOperationalStatus(StorageLocation loc) {
        int capacity = loc.getCapacity() != null ? loc.getCapacity() : 0;
        int used = loc.getUsedCapacity() != null ? loc.getUsedCapacity() : 0;

        if (used <= 0) {
            loc.setStatus("EMPTY");
        } else if (capacity > 0 && used >= capacity) {
            loc.setStatus("FULL");
        } else {
            loc.setStatus("ACTIVE");
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}