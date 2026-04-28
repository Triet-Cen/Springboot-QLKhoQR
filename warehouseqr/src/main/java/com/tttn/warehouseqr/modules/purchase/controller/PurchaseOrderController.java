package com.tttn.warehouseqr.modules.purchase.controller;

import com.tttn.warehouseqr.common.util.SecurityUtils;
import com.tttn.warehouseqr.modules.masterdata.product.repository.ProductRepository;
import com.tttn.warehouseqr.modules.masterdata.supplier.repository.SupplierRepository;
import com.tttn.warehouseqr.modules.masterdata.warehouse.repository.WarehouseRepository;
import com.tttn.warehouseqr.modules.purchase.entity.PurchaseOrders;
import com.tttn.warehouseqr.modules.purchase.repository.PurchaseOrderItemRepository;
import com.tttn.warehouseqr.modules.purchase.repository.PurchaseOrdersRepository;
import com.tttn.warehouseqr.modules.purchase.service.PurchaseOrderService;
import com.tttn.warehouseqr.utils.QrCodeUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/manager")
public class PurchaseOrderController {
    private final PurchaseOrderService poService;
    private final PurchaseOrdersRepository poRepo;
    private final SupplierRepository supplierRepo;
    private final WarehouseRepository warehouseRepo;
    private final ProductRepository productRepo;
    private final PurchaseOrderItemRepository poItemRepo;

    public PurchaseOrderController(PurchaseOrderService poService, PurchaseOrdersRepository poRepo,
                                   SupplierRepository supplierRepo, WarehouseRepository warehouseRepo,
                                   ProductRepository productRepo, PurchaseOrderItemRepository poItemRepo) {
        this.poService = poService;
        this.poRepo = poRepo;
        this.supplierRepo = supplierRepo;
        this.warehouseRepo = warehouseRepo;
        this.productRepo = productRepo;
        this.poItemRepo = poItemRepo;
    }

    // 1. Hiển thị trang danh sách và Form
    @GetMapping("/purchase-orders")
    public String showPurchaseOrderPage(Model model) {
        // Lấy danh sách từ DB lên
        List<PurchaseOrders> poList = poRepo.findAllByOrderByIdDesc();

        // --- MÁY PHÁT HIỆN NÓI DỐI ---
        System.out.println("========== BẮT ĐẦU KIỂM TRA PO ==========");
        for (PurchaseOrders po : poList) {
            System.out.println("Mã PO: " + po.getPoCode() + " | Trạng thái đang giữ trong Java: [" + po.getStatus() + "]");
        }
        System.out.println("===========================================");
        // -----------------------------


        model.addAttribute("purchaseOrders", poRepo.findAllByOrderByIdDesc());
        model.addAttribute("suppliers", supplierRepo.findAll());
        model.addAttribute("warehouses", warehouseRepo.findAll());
        return "inboundOutboundTransfer/purchase-orders";
    }

    @GetMapping("/purchase-orders/create")
    public String showCreatePurchaseOrderForm(Model model) {
        model.addAttribute("suppliers", supplierRepo.findAll());
        model.addAttribute("warehouses", warehouseRepo.findAll());
        model.addAttribute("products", productRepo.findAll());
        return "inboundOutboundTransfer/purchase-order-form";
    }

    @PostMapping("/purchase-orders/create")
    public String createPurchaseOrder(@RequestParam Long supplierId,
                                      @RequestParam Long warehouseId,
                                      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime expectedDeliveryDate,
                                      @RequestParam(required = false) String notes,
                                      @RequestParam(value = "productIds", required = false) List<String> productIds,
                                      @RequestParam(value = "orderedQtys", required = false) List<String> orderedQtys,
                                      @RequestParam(value = "unitPrices", required = false) List<String> unitPrices,
                                      @RequestParam(value = "batchIds", required = false) List<String> batchIds,
                                      RedirectAttributes redirectAttributes) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            poService.createManualPurchaseOrder(supplierId, warehouseId, expectedDeliveryDate, notes,
                    productIds, orderedQtys, unitPrices, batchIds, userId);
            redirectAttributes.addFlashAttribute("success", "Tạo đơn mua hàng thành công!");
            return "redirect:/manager/purchase-orders";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/manager/purchase-orders/create";
        }
    }

    // 2. Xử lý Form Submit (Import CSV)
    @PostMapping("/purchase-orders/import")
    public String handleImportCsv(
            @RequestParam("file") MultipartFile file,
            @RequestParam("supplierId") Long supplierId,
            @RequestParam("warehouseId") Long warehouseId,
            RedirectAttributes redirectAttributes) {

        try {
            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Vui lòng chọn file CSV!");
                return "redirect:/manager/purchase-orders";
            }
            //Truyền userId vào service
            Long userId = SecurityUtils.getCurrentUserId();
            // Gọi service xử lý
            poService.createPoFromCsv(file, supplierId, warehouseId,userId);

            // Gửi thông báo thành công cho trang sau khi redirect
            redirectAttributes.addFlashAttribute("success", "Tạo đơn mua hàng thành công!");

        } catch (Exception e) {
            // Gửi thông báo lỗi
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        // Redirect về lại trang danh sách (PRG Pattern)
        return "redirect:/manager/purchase-orders";
    }

    @GetMapping("/api/purchase-orders/qr-base64/{poCode}")
    @ResponseBody // Trả về text thuần (chuỗi Base64)
    public String getQrBase64(@PathVariable String poCode) {
        // Gọi Util của bạn, kích thước 300x300
        return QrCodeUtil.GenerateQRCodeBase64(poCode, 300, 300);
    }

    @GetMapping("/api/purchase-orders/{id}/items")
    @ResponseBody
    public ResponseEntity<?> getPoItemsForScan(@PathVariable Long id) {
        List<Object[]> items = poItemRepo.findItemsByPoIdForScan(id);
        // Map sang List các Map hoặc DTO để JS dễ đọc
        List<Map<String, Object>> result = items.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("productId", row[0]);
            map.put("productName", row[1]);
            map.put("sku", row[2]);
            map.put("expectedQty", row[3]); // Đây là số lượng dự kiến lấy từ PO
            map.put("price", row[4]);
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
}
