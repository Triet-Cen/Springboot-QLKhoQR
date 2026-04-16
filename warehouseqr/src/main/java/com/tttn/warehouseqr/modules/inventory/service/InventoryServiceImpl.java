package com.tttn.warehouseqr.modules.inventory.service;

import com.tttn.warehouseqr.modules.inventory.dto.InventoryDashboardDto;
import com.tttn.warehouseqr.modules.inventory.dto.InventoryDetailDto;
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

        // Tổng số dòng sản phẩm
        stats.setTotalProducts(items.size());

        long lowStockCount = 0;

        // Khởi tạo bằng BigDecimal.ZERO thay vì 0.0
        BigDecimal totalQty = BigDecimal.ZERO;
        BigDecimal totalVal = BigDecimal.ZERO;

        for (InventoryItemDto item : items) {

            // Lấy giá trị an toàn, nếu null thì gán là 0
            BigDecimal qty = (item.getTotalQuantity() != null) ? item.getTotalQuantity() : BigDecimal.ZERO;
            BigDecimal val = (item.getTotalValue() != null) ? item.getTotalValue() : BigDecimal.ZERO;

            // Dùng phương thức .add() để cộng dồn, và phải gán ngược lại vào biến
            totalQty = totalQty.add(qty);
            totalVal = totalVal.add(val);

            if (item.isLowStock()) {
                lowStockCount++;
            }
        }

        // Set thẳng vào Dto mà không cần chuyển đổi (valueOf) nữa
        stats.setTotalQuantity(totalQty);
        stats.setTotalInventoryValue(totalVal);
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

    @Override
    public List<InventoryDetailDto> getProductDetails(Long productId, Long warehouseId) {
        // Gọi query lấy chi tiết theo Vị trí và Lô hàng
        return productRepository.getProductInventoryDetails(productId, warehouseId);
    }
}