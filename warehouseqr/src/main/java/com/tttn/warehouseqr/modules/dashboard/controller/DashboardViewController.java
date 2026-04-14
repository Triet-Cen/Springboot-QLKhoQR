package com.tttn.warehouseqr.modules.dashboard.controller;

import com.tttn.warehouseqr.modules.inventory.dto.InventoryDashboardDto;
import com.tttn.warehouseqr.modules.inventory.dto.InventoryItemDto;
import com.tttn.warehouseqr.modules.inventory.service.InventoryService;
import com.tttn.warehouseqr.modules.masterdata.supplier.entity.Supplier;
import com.tttn.warehouseqr.modules.masterdata.supplier.repository.SupplierRepository; // Nhớ import cái này
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class DashboardViewController {

    private final InventoryService inventoryService;

    // BỔ SUNG: Khai báo repository để Spring có thể tự động tiêm vào
    private final SupplierRepository supplierRepository;

    @GetMapping("/admin/dashboard")
    public String showMainDashboard(Model model) {
        // 1. Lấy danh sách inventory items
        List<InventoryItemDto> items = inventoryService.getInventoryItems(null, null);

        // 2. Tính toán các con số thống kê
        InventoryDashboardDto stats = inventoryService.getDashboardStats(items);

        // 3. Đưa dữ liệu sang template
        model.addAttribute("stats", stats);

        // 4. Lấy danh sách Nhà cung cấp truyền xuống HTML
        List<Supplier> suppliers = supplierRepository.findAll();
        model.addAttribute("suppliers", suppliers);

        return "./dashboard/dashboard_view";
    }
}