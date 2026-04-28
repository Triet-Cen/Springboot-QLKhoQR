// StocktakeController.java
package com.tttn.warehouseqr.modules.stocktake.controller;

import com.tttn.warehouseqr.modules.inventory.entity.InventoryLocationBalance;
import com.tttn.warehouseqr.modules.inventory.repository.InventoryLocationBalanceRepository;
import com.tttn.warehouseqr.modules.masterdata.product.entity.Product;
import com.tttn.warehouseqr.modules.masterdata.product.entity.ProductBatch;
import com.tttn.warehouseqr.modules.masterdata.product.repository.ProductBatchRepository;
import com.tttn.warehouseqr.modules.masterdata.product.repository.ProductRepository;
import com.tttn.warehouseqr.modules.masterdata.warehouse.entity.Warehouse;
import com.tttn.warehouseqr.modules.masterdata.warehouse.repository.WarehouseRepository;
import com.tttn.warehouseqr.modules.stocktake.dto.*;
import com.tttn.warehouseqr.modules.stocktake.entity.StocktakeItem;
import com.tttn.warehouseqr.modules.stocktake.entity.StocktakeSession;
import com.tttn.warehouseqr.modules.stocktake.repository.StocktakeItemRepository;
import com.tttn.warehouseqr.modules.stocktake.repository.StocktakeSessionRepository;
import com.tttn.warehouseqr.modules.stocktake.service.StocktakeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/stocktake")
@RequiredArgsConstructor
public class StocktakeController {
    private final StocktakeService stocktakeService;
    private final StocktakeSessionRepository sessionRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryLocationBalanceRepository balanceRepository;
    private final StocktakeItemRepository stocktakeItemRepository;
    private final ProductBatchRepository batchRepository;
    private final ProductRepository productRepository;

    @GetMapping
    public String dashboard(@RequestParam(required = false) Long sessionId,
                            @RequestParam(required = false) Long warehouseId,
                            Model model) {

        // 1. Load danh sách kho cho bộ lọc
        List<Warehouse> warehouses = warehouseRepository.findAll();
        model.addAttribute("warehouses", warehouses);

        // 2. Nếu người dùng không chọn kho, mặc định lấy kho đầu tiên
        if (warehouseId == null && !warehouses.isEmpty()) {
            warehouseId = warehouses.get(0).getWarehouseId();
        }
        model.addAttribute("selectedWarehouseId", warehouseId);

        // 3. Nếu không truyền sessionId, tìm Session mới nhất của Kho đang được chọn
        if (sessionId == null && warehouseId != null) {
            // Cần thêm hàm findTopByWarehouseIdOrderByCreatedAtDesc trong Repository
            sessionId = sessionRepository.findTopByWarehouseIdOrderByCreatedAtDesc(warehouseId)
                    .map(StocktakeSession::getId).orElse(null);
        }

        // 4. Load dữ liệu Dashboard nếu có Session
        if (sessionId != null) {
            model.addAttribute("dashboard", stocktakeService.getDashboardStats(sessionId));
            model.addAttribute("compareList", stocktakeService.getCompareData(sessionId));
        }

        // 5. Cảnh báo tồn kho / hết hạn CHỈ LẤY THEO KHO ĐANG CHỌN
        if (warehouseId != null) {
            model.addAttribute("lowStockList", stocktakeService.getLowStockItems(warehouseId));
            model.addAttribute("expiryList", stocktakeService.getExpiryWarningItems(warehouseId));
        }

        model.addAttribute("sessionId", sessionId);
        return "stocktake/stocktake-dashboard";
    }

    @GetMapping("/scan")
    public String scanPage(@RequestParam Long sessionId, Model model) {
        model.addAttribute("sessionId", sessionId);
        return "stocktake/stocktake-scan";
    }

    @PostMapping("/scan")
    @ResponseBody
    public ResponseEntity<?> scan(@RequestBody ScanRequest request) {
        try {
            stocktakeService.processScan(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/create-session")
    public String createSessionForm(Model model) {
        model.addAttribute("warehouses", warehouseRepository.findAll());
        return "stocktake/create-session";
    }

    @PostMapping("/create-session")
    @ResponseBody
    public ResponseEntity<?> createSessionApi(@RequestBody Map<String, Object> payload) {
        Long warehouseId = Long.valueOf(payload.get("warehouseId").toString());
        String sessionCode = (String) payload.get("sessionCode");
        Long currentUserId = com.tttn.warehouseqr.common.util.SecurityUtils.getCurrentUserId();

        // Tạo session và items (logic như đã viết)
        StocktakeSession session = new StocktakeSession();
        session.setSessionCode(sessionCode != null ? sessionCode : "ST-" + System.currentTimeMillis());

        session.setCreatedBy(currentUserId);

        session.setCreatedAt(LocalDateTime.now());
        session.setStatus("IN_PROGRESS");
        session.setWarehouseId(warehouseId);
        session = sessionRepository.save(session);

        // Tạo stocktake_items từ inventory_location_balances
        List<InventoryLocationBalance> balances = balanceRepository.findByWarehouseId(warehouseId);
        for (InventoryLocationBalance bal : balances) {
            StocktakeItem item = new StocktakeItem();
            item.setSessionId(session.getId());
            item.setSystemQty(bal.getQty());
            item.setActualQty(BigDecimal.ZERO);

            // FIX BUG: Chênh lệch ban đầu = 0 - Số lượng hệ thống
            item.setVarianceQty(BigDecimal.ZERO.subtract(bal.getQty()));

            item.setLocationId(bal.getLocationId());
            item.setProductId(bal.getProductId());
            item.setBatchId(bal.getBatchId());
            stocktakeItemRepository.save(item);
        }

        return ResponseEntity.ok(Map.of("sessionId", session.getId(), "sessionCode", session.getSessionCode()));
    }


    @GetMapping("/verify-item")
    public ResponseEntity<?> verifyItemOnLocation(
            @RequestParam Long sessionId,
            @RequestParam String locationQr,
            @RequestParam String productQr) {
        try {
            // 1. Phân tách mã Sản phẩm giống hệt lúc quét
            String[] parts = productQr.split("\\|");
            String sku = parts[0];
            String lotCode = parts.length > 1 ? parts[1] : null;

            Product product = productRepository.findProductBySku(sku)
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại."));

            ProductBatch batch = null;
            if (lotCode != null && !lotCode.trim().isEmpty()) {
                batch = batchRepository.findByLotCodeAndProduct(lotCode, product).orElse(null);
            }

            // 2. Tìm kiếm xem CÓ tồn tại dòng dữ liệu nào nối giữa Sản phẩm này và Kệ này không
            stocktakeItemRepository.findItemByLocationAndProduct(
                            sessionId, locationQr, product.getProduct_id(), batch != null ? batch.getBatchId() : null)
                    .orElseThrow(() -> new RuntimeException("Mặt hàng này KHÔNG CÓ trên Kệ " + locationQr));

            // Nếu code chạy đến đây nghĩa là có tồn tại -> Trả về OK
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            // Trả về lỗi 400 Bad Request kèm câu thông báo
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}