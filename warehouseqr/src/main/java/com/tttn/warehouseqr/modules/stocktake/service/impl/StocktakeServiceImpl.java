// StocktakeServiceImpl.java
package com.tttn.warehouseqr.modules.stocktake.service.impl;

import com.tttn.warehouseqr.modules.inventory.repository.InventoryLocationBalanceRepository;
import com.tttn.warehouseqr.modules.masterdata.product.repository.ProductBatchRepository;
import com.tttn.warehouseqr.modules.masterdata.product.repository.ProductRepository;
import com.tttn.warehouseqr.modules.stocktake.dto.*;
import com.tttn.warehouseqr.modules.stocktake.repository.StocktakeItemRepository;
import com.tttn.warehouseqr.modules.stocktake.service.StocktakeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StocktakeServiceImpl implements StocktakeService {

    private final StocktakeItemRepository stocktakeItemRepository;
    private final ProductRepository productRepository;      // cần thêm
    private final InventoryLocationBalanceRepository balanceRepository; // cần thêm
    private final ProductBatchRepository batchRepository;   // cần thêm

    @Override
    public StocktakeDashboardDto getDashboardStats(Long sessionId) {
        List<StocktakeCompareDto> compares = stocktakeItemRepository.getCompareData(sessionId);
        long totalScanned = compares.size();
        long varianceCount = compares.stream()
                .filter(c -> c.getVarianceQty() != null && c.getVarianceQty().compareTo(BigDecimal.ZERO) != 0)
                .count();
        double accuracy = totalScanned == 0 ? 100.0 :
                (double)(totalScanned - varianceCount) / totalScanned * 100;
        double completionPercent = 100.0; // giả định hoàn thành 100% vì đã có dữ liệu quét

        StocktakeDashboardDto dto = new StocktakeDashboardDto();
        dto.setTotalScanned(totalScanned);
        dto.setCompletionPercent(completionPercent);
        dto.setVarianceCount(varianceCount);
        dto.setAccuracy(Math.round(accuracy * 10) / 10.0);
        dto.setAccuracyChange(2.3); // demo
        return dto;
    }

    @Override
    public List<StocktakeCompareDto> getCompareData(Long sessionId) {
        return stocktakeItemRepository.getCompareData(sessionId);
    }

    @Override
    public List<LowStockDto> getLowStockItems(Long warehouseId) {
        // Giả sử có bảng inventory_location_balances, tính tổng qty theo product, warehouse
        // Lấy các sản phẩm có tổng tồn <= min_stock
        return productRepository.findLowStockByWarehouse(warehouseId);
    }

    @Override
    public List<ExpiryWarningDto> getExpiryWarningItems(Long warehouseId) {
        // Lấy các batch có expiry_date trong vòng 30 ngày tới
        LocalDate now = LocalDate.now();
        LocalDate threshold = now.plusDays(30);
        return batchRepository.findExpiringBatches(warehouseId, now, threshold);
    }
}