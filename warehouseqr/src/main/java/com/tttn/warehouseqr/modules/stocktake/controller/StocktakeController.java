// StocktakeController.java
package com.tttn.warehouseqr.modules.stocktake.controller;

import com.tttn.warehouseqr.modules.stocktake.dto.*;
import com.tttn.warehouseqr.modules.stocktake.service.StocktakeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/stocktake")
@RequiredArgsConstructor
public class StocktakeController {

    private final StocktakeService stocktakeService;

    // Mặc định lấy session mới nhất hoặc tạo mới
    @GetMapping
    public String viewStocktakeDashboard(@RequestParam(value = "sessionId", required = false) Long sessionId,
                                         Model model) {
        if (sessionId == null) {
            sessionId = getLatestSessionId(); // cần implement
        }
        StocktakeDashboardDto dashboard = stocktakeService.getDashboardStats(sessionId);
        List<StocktakeCompareDto> compareList = stocktakeService.getCompareData(sessionId);
        List<LowStockDto> lowStockList = stocktakeService.getLowStockItems(1L); // warehouseId mặc định
        List<ExpiryWarningDto> expiryList = stocktakeService.getExpiryWarningItems(1L);

        model.addAttribute("dashboard", dashboard);
        model.addAttribute("compareList", compareList);
        model.addAttribute("lowStockList", lowStockList);
        model.addAttribute("expiryList", expiryList);
        model.addAttribute("sessionId", sessionId);
        return "stocktake/stocktake-dashboard";
    }

    private Long getLatestSessionId() {
        // Lấy session mới nhất từ DB, nếu chưa có thì tạo mới
        return 1L; // tạm thời
    }
}