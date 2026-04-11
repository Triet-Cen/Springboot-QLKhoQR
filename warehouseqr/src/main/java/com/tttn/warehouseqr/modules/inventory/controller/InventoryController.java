package com.tttn.warehouseqr.modules.inventory.controller;

import com.tttn.warehouseqr.modules.inventory.dto.InventoryDashboardDto;
import com.tttn.warehouseqr.modules.inventory.dto.InventoryItemDto;
import com.tttn.warehouseqr.modules.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public String viewInventoryDashboard(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        // Lấy danh sách sản phẩm và tồn kho
        List<InventoryItemDto> items = inventoryService.getInventoryItems(keyword);

        // Tính toán số liệu cho 4 thẻ thống kê
        InventoryDashboardDto dashboard = inventoryService.getDashboardStats(items);

        // Đẩy data xuống View
        model.addAttribute("items", items);
        model.addAttribute("dashboard", dashboard);
        model.addAttribute("keyword", keyword);

        return "inventory/inventory-dashboard";
    }
}