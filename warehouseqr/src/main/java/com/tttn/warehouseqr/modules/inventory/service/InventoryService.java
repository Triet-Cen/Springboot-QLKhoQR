package com.tttn.warehouseqr.modules.inventory.service;

import com.tttn.warehouseqr.modules.inventory.dto.InventoryDashboardDto;
import com.tttn.warehouseqr.modules.inventory.dto.InventoryItemDto;

import java.util.List;

public interface InventoryService {

    List<InventoryItemDto> getInventoryItems(String keyword, Long warehouseId);

    InventoryDashboardDto getDashboardStats(List<InventoryItemDto> items);
}