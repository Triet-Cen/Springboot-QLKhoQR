package com.tttn.warehouseqr.modules.inventory.services;

import java.math.BigDecimal;

public interface InventoryServices {
    void reduceStock(Long warehouseId, Long locationId, Long productId, Long batchId, BigDecimal qty);
    void addStock(Long warehouseId, Long locationId, Long productId, Long batchId, BigDecimal qty);
}
