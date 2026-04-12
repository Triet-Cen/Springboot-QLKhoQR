package com.tttn.warehouseqr.modules.inventory.service;

import com.tttn.warehouseqr.modules.inventory.dto.InventoryDashboardDto;
import com.tttn.warehouseqr.modules.inventory.dto.InventoryItemDto;

import com.tttn.warehouseqr.modules.inventory.entity.InventoryLocationBalance;
import com.tttn.warehouseqr.modules.inventory.repository.InventoryLocationBalanceRepository;
import com.tttn.warehouseqr.modules.inventory.service.InventoryService;
import com.tttn.warehouseqr.modules.masterdata.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final ProductRepository productRepository;

    private final InventoryLocationBalanceRepository balanceRepository;

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

    @Override
    public void reduceStock(Long warehouseId, Long locationId, Long productId, Long batchId, BigDecimal qty) {
        InventoryLocationBalance balance = balanceRepository
                .findByWarehouseIdAndLocationIdAndProductIdAndBatchId(warehouseId, locationId, productId, batchId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hàng trong kho nguồn tại vị trí này!"));

        if (balance.getQty().compareTo((qty)) < 0) {
            throw new RuntimeException("Số lượng tồn kho không đủ để điều chuyển!");
        }

        balance.setQty(balance.getQty().subtract(qty));
        balance.setUpdateAt(LocalDateTime.now());
        balanceRepository.save(balance);
    }

    @Override
    public void addStock(Long warehouseId, Long locationId, Long productId, Long batchId, BigDecimal qty) {
        InventoryLocationBalance balance = balanceRepository
                .findByWarehouseIdAndLocationIdAndProductIdAndBatchId(warehouseId, locationId, productId, batchId)
                .orElseGet(() -> {
                    InventoryLocationBalance newBalance = new InventoryLocationBalance();
                    newBalance.setWarehouseId(warehouseId);
                    newBalance.setLocationId(locationId);
                    newBalance.setProductId(productId);
                    newBalance.setBatchId(batchId);
                    newBalance.setQty(BigDecimal.ZERO); // Khởi tạo bằng 0
                    return newBalance;
                });

        // Thực hiện cộng: qty = currentQty + inputQty
        balance.setQty(balance.getQty().add(qty));
        balance.setUpdateAt(LocalDateTime.now());
        balanceRepository.save(balance);
    }
}