package com.tttn.warehouseqr.modules.Location.service;

import com.tttn.warehouseqr.modules.Location.entity.StorageLocation;

import java.util.List;

public interface StorageLocationService {

    List<StorageLocation> findAll();

    List<StorageLocation> search(String keyword, Long warehouseId, String status);

    StorageLocation findById(Long id);

    StorageLocation save(StorageLocation location);

    StorageLocation update(Long id, StorageLocation location);
}