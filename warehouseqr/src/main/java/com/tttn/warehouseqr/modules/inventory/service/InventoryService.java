package com.tttn.warehouseqr.modules.inventory.service;

import com.tttn.warehouseqr.modules.inventory.dto.InventoryDashboardDto;
import com.tttn.warehouseqr.modules.inventory.dto.InventoryDetailDto;
import com.tttn.warehouseqr.modules.inventory.dto.InventoryItemDto;

import java.math.BigDecimal;
import java.util.List;

public interface InventoryService {

    List<InventoryItemDto> getInventoryItems(String keyword, Long warehouseId);

    InventoryDashboardDto getDashboardStats(List<InventoryItemDto> items);

    void reduceStock(Long warehouseId, Long locationId, Long productId, Long batchId, BigDecimal qty);
    void addStock(Long warehouseId, Long locationId, Long productId, Long batchId, BigDecimal qty);

    //lấy chi tiết cho Drill-down
    List<InventoryDetailDto> getProductDetails(Long productId, Long warehouseId);
}