package com.tttn.warehouseqr.modules.stocktake.service.impl;

import com.tttn.warehouseqr.common.exception.AppException;
import com.tttn.warehouseqr.common.exception.ErrorCode;
import com.tttn.warehouseqr.modules.inventory.repository.InventoryLocationBalanceRepository;
import com.tttn.warehouseqr.modules.masterdata.product.entity.Product;
import com.tttn.warehouseqr.modules.masterdata.product.entity.ProductBatch;
import com.tttn.warehouseqr.modules.masterdata.product.repository.ProductBatchRepository;
import com.tttn.warehouseqr.modules.masterdata.product.repository.ProductRepository;
import com.tttn.warehouseqr.modules.masterdata.warehouse.repository.WarehouseLocationRepository;
import com.tttn.warehouseqr.modules.stocktake.dto.*;
import com.tttn.warehouseqr.modules.stocktake.entity.StocktakeItem;
import com.tttn.warehouseqr.modules.stocktake.repository.StocktakeItemRepository;
import com.tttn.warehouseqr.modules.stocktake.service.StocktakeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StocktakeServiceImpl implements StocktakeService {
    private final StocktakeItemRepository stocktakeItemRepository;
    private final ProductRepository productRepository;
    private final WarehouseLocationRepository locationRepository;
    private final ProductBatchRepository batchRepository;
    private final InventoryLocationBalanceRepository balanceRepository;

    @Override
    public StocktakeDashboardDto getDashboardStats(Long sessionId) {
        List<StocktakeItem> items = stocktakeItemRepository.findBySessionId(sessionId);
        long total = items.size();
        long scanned = items.stream().filter(i -> i.getActualQty() != null && i.getActualQty().compareTo(BigDecimal.ZERO) > 0).count();
        long variance = items.stream().filter(i -> i.getVarianceQty() != null && i.getVarianceQty().compareTo(BigDecimal.ZERO) != 0).count();
        double percent = total == 0 ? 0 : (double) scanned / total * 100;
        StocktakeDashboardDto dto = new StocktakeDashboardDto();
        dto.setTotalProducts(total);
        dto.setTotalScanned(scanned);
        dto.setCompletionPercent(Math.round(percent * 10) / 10.0);
        dto.setVarianceCount(variance);
        return dto;
    }

    @Override
    public List<StocktakeCompareDto> getCompareData(Long sessionId) {
        return stocktakeItemRepository.getCompareData(sessionId);
    }

    @Override
    public void processScan(ScanRequest request) {
        // 1. Phân tách mã Sản phẩm
        String[] parts = request.getProductQr().split("\\|");
        String sku = parts[0];
        String lotCode = parts.length > 1 ? parts[1] : null;

        Product product = productRepository.findProductBySku(sku)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        ProductBatch batch = null;
        if (lotCode != null && !lotCode.trim().isEmpty()) {
            batch = batchRepository.findByLotCodeAndProduct(lotCode, product).orElse(null);
        }

        // 2. Tìm dòng kiểm kê theo CẢ SESSION, PRODUCT VÀ LOCATION
        // Lưu ý: Bạn cần viết thêm hàm này trong StocktakeItemRepository
        StocktakeItem item = stocktakeItemRepository.findItemByLocationAndProduct(
                        request.getSessionId(),
                        request.getLocationQr(), // Tìm đích danh mã kệ này
                        product.getProduct_id(),
                        batch != null ? batch.getBatchId() : null)
                .orElseThrow(() -> new RuntimeException("Mặt hàng này không có trên Kệ " + request.getLocationQr() + " theo dữ liệu hệ thống!"));

        BigDecimal scanQty = (request.getQty() != null) ? request.getQty() : BigDecimal.ONE;
        BigDecimal currentActual = (item.getActualQty() == null) ? BigDecimal.ZERO : item.getActualQty();
        BigDecimal newActual = currentActual.add(scanQty);

        // 3. LOGIC CẢNH BÁO CHÊNH LỆCH
        // Nếu số đếm mới KHÁC số hệ thống VÀ nhân viên chưa gửi cờ xác nhận
        if (newActual.compareTo(item.getSystemQty()) != 0 && !request.isConfirmVariance()) {
            // Ném ra một lỗi đặc biệt để Frontend nhận biết
            throw new RuntimeException("VARIANCE_WARNING|" +
                    "Phát hiện chênh lệch! Số lượng bạn đếm đang khác với dự kiến. Bạn có chắc chắn muốn ghi nhận " + newActual + " sản phẩm?");
        }

        // 4. Lưu dữ liệu nếu khớp, hoặc nếu lệch nhưng đã có cờ xác nhận
        item.setActualQty(newActual);
        item.setVarianceQty(newActual.subtract(item.getSystemQty()));
        stocktakeItemRepository.save(item);
    }

    @Override
    public List<LowStockDto> getLowStockItems(Long warehouseId) {
        return productRepository.findLowStockByWarehouse(warehouseId);
    }

    @Override
    public List<ExpiryWarningDto> getExpiryWarningItems(Long warehouseId) {
        LocalDate now = LocalDate.now();
        LocalDate threshold = now.plusDays(30);
        List<ExpiryWarningDto> dtos = batchRepository.findExpiringBatches(warehouseId, now, threshold);
        dtos.forEach(dto -> dto.setDaysRemaining(ChronoUnit.DAYS.between(now, dto.getExpiryDate())));
        return dtos;
    }
}