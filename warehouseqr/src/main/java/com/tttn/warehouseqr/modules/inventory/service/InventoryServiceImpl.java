package com.tttn.warehouseqr.modules.inventory.service;

import com.tttn.warehouseqr.modules.inventory.dto.InventoryDashboardDto;
import com.tttn.warehouseqr.modules.inventory.dto.InventoryItemDto;
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
        return productRepository.getInventoryReport(keyword, warehouseId);
    }

    @Override
    public InventoryDashboardDto getDashboardStats(List<InventoryItemDto> items) {
        InventoryDashboardDto stats = new InventoryDashboardDto();

        // Tổng số loại sản phẩm
        stats.setTotalProducts(items.size());

        long lowStockCount = 0;
        double totalQty = 0.0;
        double totalVal = 0.0;

        for (InventoryItemDto item : items) {

            totalQty += (item.getTotalQuantity() != null ? item.getTotalQuantity() : 0.0);
            totalVal += (item.getTotalValue() != null ? item.getTotalValue() : 0.0);


            if (item.isLowStock()) {
                lowStockCount++;
            }
        }

        // Chuyển ngược lại về BigDecimal để set vào Dto nếu DashboardDto yêu cầu BigDecimal
        stats.setTotalQuantity(BigDecimal.valueOf(totalQty));
        stats.setTotalInventoryValue(BigDecimal.valueOf(totalVal));
        stats.setLowStockWarnings(lowStockCount);

        return stats;
    }
}