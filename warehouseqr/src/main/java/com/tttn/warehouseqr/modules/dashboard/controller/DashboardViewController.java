package com.tttn.warehouseqr.modules.dashboard.controller;

import com.tttn.warehouseqr.modules.inventory.dto.InventoryDashboardDto;
import com.tttn.warehouseqr.modules.inventory.dto.InventoryItemDto;
import com.tttn.warehouseqr.modules.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class DashboardViewController {

    private final InventoryService inventoryService;

    @GetMapping("/admin/dashboard")
    public String showMainDashboard(Model model) {
        // 1. Lấy danh sách inventory items (không lọc theo keyword hay warehouse để lấy tổng)
        List<InventoryItemDto> items = inventoryService.getInventoryItems(null, null);

        // 2. Tận dụng hàm getDashboardStats có sẵn trong InventoryServiceImpl để tính toán các con số
        InventoryDashboardDto stats = inventoryService.getDashboardStats(items);

        // 3. Đưa dữ liệu sang template dashboard_view.html
        model.addAttribute("stats", stats);

        return "./dashboard/dashboard_view";
    }
}
