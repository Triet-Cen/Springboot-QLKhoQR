package com.tttn.warehouseqr.modules.dashboard.controller;

import com.tttn.warehouseqr.modules.inventory.dto.InventoryDashboardDto;
import com.tttn.warehouseqr.modules.inventory.dto.InventoryItemDto;
import com.tttn.warehouseqr.modules.inventory.service.InventoryService;
import com.tttn.warehouseqr.modules.inbound.repository.InboundReceiptRepository;
import com.tttn.warehouseqr.modules.outbound.repository.OutboundReceiptRepository;
import com.tttn.warehouseqr.modules.masterdata.supplier.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class DashboardViewController {

    private final InventoryService inventoryService;
    private final SupplierRepository supplierRepository;
    private final InboundReceiptRepository inboundRepository;
    private final OutboundReceiptRepository outboundRepository;

    @GetMapping("/admin/dashboard")
    public String showMainDashboard(Model model) {
        // 1. Lấy thống kê tổng quát
        List<InventoryItemDto> items = inventoryService.getInventoryItems(null, null);
        InventoryDashboardDto stats = inventoryService.getDashboardStats(items);
        model.addAttribute("stats", stats);

        // 2. Lọc hàng tồn kho thấp (Dùng totalQuantity và compareTo như file DTO của bạn)
        List<InventoryItemDto> lowStockItems = items.stream()
                .filter(item -> item.getTotalQuantity() != null &&
                        item.getTotalQuantity().compareTo(new BigDecimal("10")) < 0)
                .limit(10)
                .collect(Collectors.toList());
        model.addAttribute("lowStockItems", lowStockItems);

        // 3. Danh sách Nhà cung cấp
        model.addAttribute("suppliers", supplierRepository.findAll());

        // 4. Cây Nhập kho
        model.addAttribute("recentInbounds",
                inboundRepository.findByStatusInOrderByCreatedAtDesc(Arrays.asList("COMPLETED", "PENDING")));

        // 5. Cây Xuất kho (Gửi dữ liệu cho Tree View)
        model.addAttribute("recentOutbounds", outboundRepository.findAll());

        return "./dashboard/dashboard_view";
    }
}