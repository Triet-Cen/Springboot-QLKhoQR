package com.tttn.warehouseqr.modules.inventory.controller;

import com.tttn.warehouseqr.modules.inventory.dto.InventoryDashboardDto;
import com.tttn.warehouseqr.modules.inventory.dto.InventoryDetailDto;
import com.tttn.warehouseqr.modules.inventory.dto.InventoryItemDto;
import com.tttn.warehouseqr.modules.inventory.service.InventoryService;
import com.tttn.warehouseqr.modules.masterdata.warehouse.entity.Warehouse;
import com.tttn.warehouseqr.modules.masterdata.warehouse.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/details/{productId}")
    public String getInventoryDetails(@PathVariable Long productId,
                                      @RequestParam(required = false) Long warehouseId, // CỰC KỲ QUAN TRỌNG: required = false
                                      Model model) {

        // Gọi Service lấy chi tiết. Hàm Repository
        // để xử lý "warehouseId IS NULL" rồi nên truyền null vào không sao cả.
        List<InventoryDetailDto> details = inventoryService.getProductDetails(productId, warehouseId);

        model.addAttribute("details", details);

        // Cú pháp này báo cho Thymeleaf biết: Chỉ lấy đúng cái đoạn
        // <div th:fragment="detail-fragment"> để trả về, ĐỪNG trả về cả trang.
        return "inventory/inventory-dashboard :: detail-fragment";
    }
}