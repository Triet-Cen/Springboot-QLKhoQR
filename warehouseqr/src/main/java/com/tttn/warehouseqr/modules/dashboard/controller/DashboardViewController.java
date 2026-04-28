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

        List<InventoryItemDto> items = inventoryService.getInventoryItems(null, null);
        InventoryDashboardDto stats = inventoryService.getDashboardStats(items);
        model.addAttribute("stats", stats);


        List<InventoryItemDto> lowStockItems = items.stream()
                .filter(item -> item.getTotalQuantity() != null &&
                        item.getTotalQuantity().compareTo(new BigDecimal("10")) < 0)
                .limit(10)
                .collect(Collectors.toList());
        model.addAttribute("lowStockItems", lowStockItems);


        model.addAttribute("suppliers", supplierRepository.findAll());


        model.addAttribute("recentInbounds",
                inboundRepository.findByStatusInOrderByCreatedAtDesc(Arrays.asList("COMPLETED", "PENDING")));


        model.addAttribute("recentOutbounds", outboundRepository.findAll());

        return "./dashboard/dashboard_view";
    }
}