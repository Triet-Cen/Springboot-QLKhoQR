package com.tttn.warehouseqr.modules.inventory.controller;

import com.tttn.warehouseqr.modules.inventory.dto.InventoryDashboardDto;
import com.tttn.warehouseqr.modules.inventory.dto.InventoryItemDto;
import com.tttn.warehouseqr.modules.inventory.service.InventoryService;
import com.tttn.warehouseqr.modules.masterdata.warehouse.entity.Warehouse;
import com.tttn.warehouseqr.modules.masterdata.warehouse.repository.WarehouseRepository;
import com.tttn.warehouseqr.modules.masterdata.warehouse.service.WarehouseService;
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
    private final WarehouseRepository warehouseRepository; // cần tạo repository này

    @GetMapping
    public String viewInventoryDashboard(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "warehouseId", required = false) Long warehouseId,
            Model model) {

        // Lấy danh sách tất cả kho để hiển thị dropdown
        List<Warehouse> warehouses = warehouseRepository.findAll();
        model.addAttribute("warehouses", warehouses);
        model.addAttribute("selectedWarehouseId", warehouseId);

        // Lấy dữ liệu tồn kho theo kho được chọn
        List<InventoryItemDto> items = inventoryService.getInventoryItems(keyword, warehouseId);
        InventoryDashboardDto dashboard = inventoryService.getDashboardStats(items);

        model.addAttribute("items", items);
        model.addAttribute("dashboard", dashboard);
        model.addAttribute("keyword", keyword);

        return "inventory/inventory-dashboard";
    }
}