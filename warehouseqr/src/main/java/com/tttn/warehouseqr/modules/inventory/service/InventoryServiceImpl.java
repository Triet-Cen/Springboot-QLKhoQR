package com.tttn.warehouseqr.modules.inventory.service;

import com.tttn.warehouseqr.modules.inventory.dto.InventoryDashboardDto;
import com.tttn.warehouseqr.modules.inventory.dto.InventoryItemDto;
import com.tttn.warehouseqr.modules.inventory.service.InventoryService;
import com.tttn.warehouseqr.modules.masterdata.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final ProductRepository productRepository;

    @Override
    public List<InventoryItemDto> getInventoryItems(String keyword, Long warehouseId) {
        // Truyền thêm warehouseId vào Repository
        return productRepository.getInventoryReport(keyword, warehouseId);
    }

    @Override
    public InventoryDashboardDto getDashboardStats(List<InventoryItemDto> items) {
        InventoryDashboardDto stats = new InventoryDashboardDto();
        stats.setTotalProducts(items.size());

        long lowStockCount = 0;
        BigDecimal totalQty = BigDecimal.ZERO;
        BigDecimal totalVal = BigDecimal.ZERO;
        for (InventoryItemDto item : items) {
            totalQty = totalQty.add(item.getTotalQuantity());
            totalVal = totalVal.add(item.getTotalValue());
            if (item.isLowStock()) {
                lowStockCount++;
            }
        }

        stats.setTotalQuantity(totalQty);
        stats.setTotalInventoryValue(totalVal);
        stats.setLowStockWarnings(lowStockCount);

        return stats;
    }
}