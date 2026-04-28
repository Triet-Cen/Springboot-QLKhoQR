
package com.tttn.warehouseqr.modules.stocktake.service;

import com.tttn.warehouseqr.modules.stocktake.dto.*;

import java.util.List;

public interface StocktakeService {
    StocktakeDashboardDto getDashboardStats(Long sessionId);
    List<StocktakeCompareDto> getCompareData(Long sessionId);
    List<LowStockDto> getLowStockItems(Long warehouseId);
    List<ExpiryWarningDto> getExpiryWarningItems(Long warehouseId);
    public void processScan(ScanRequest request);
}