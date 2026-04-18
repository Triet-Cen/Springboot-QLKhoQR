package com.tttn.warehouseqr.modules.inbound.controller;

import com.tttn.warehouseqr.common.util.SecurityUtils;
import com.tttn.warehouseqr.modules.inbound.dto.InboundRequestDTO;
import com.tttn.warehouseqr.modules.inbound.entity.InboundReceipt;
import com.tttn.warehouseqr.modules.inbound.repository.InboundReceiptRepository;
import com.tttn.warehouseqr.modules.inbound.request.InboundItemRequestDTO;
import com.tttn.warehouseqr.modules.inbound.service.InboundService;
import com.tttn.warehouseqr.modules.inbound.service.impl.InboundServiceImpl;
import com.tttn.warehouseqr.modules.masterdata.product.dto.ProductScanDTO;
import com.tttn.warehouseqr.modules.masterdata.product.service.impl.ProductService;
import com.tttn.warehouseqr.modules.purchase.entity.PurchaseOrders;
import com.tttn.warehouseqr.modules.purchase.repository.PurchaseOrdersRepository;
import com.tttn.warehouseqr.modules.purchase.service.PurchaseOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/manager/inbound")
public class InboundController {

    private final InboundServiceImpl inboundService;
    private final InboundReceiptRepository receiptRepo;
    private final SecurityUtils securityUtils;
    private final PurchaseOrderService poService;
    private final PurchaseOrdersRepository poRepo;

    public InboundController(InboundServiceImpl inboundService,
                             InboundReceiptRepository receiptRepo,
                             SecurityUtils securityUtils,
                             PurchaseOrderService poService,
                             PurchaseOrdersRepository poRepo) {
        this.inboundService = inboundService;
        this.receiptRepo = receiptRepo;
        this.securityUtils = securityUtils;
        this.poService = poService;
        this.poRepo = poRepo;
    }

    // ==========================================
    // 1. WEB VIEW METHODS (Thymeleaf)
    // ==========================================

    @GetMapping("/approval-list")
    public String showApprovalPage(Model model) {
        List<InboundReceipt> pendingList = receiptRepo.findAll().stream()
                .filter(r -> "PENDING".equals(r.getStatus()))
                .collect(Collectors.toList());

        model.addAttribute("pendingReceipts", pendingList);
        // Đảm bảo đường dẫn file HTML này là đúng trong thư mục templates
        return "inboundOutboundTransfer/manager-inbound-approval";
    }

    @PostMapping("/{id}/approve")
    public String approveReceipt(@PathVariable Long id,
                                 @RequestParam(required = false) String note,
                                 RedirectAttributes redirectAttributes) {
        try {
            inboundService.approveInboundReceipt(id, note);
            redirectAttributes.addFlashAttribute("success", "Đã phê duyệt phiếu nhập kho và cập nhật tồn kho!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi phê duyệt: " + e.getMessage());
        }
        return "redirect:/manager/inbound/approval-list"; // Thống nhất đường dẫn redirect
    }

    @PostMapping("/{id}/reject")
    public String rejectReceipt(@PathVariable Long id,
                                @RequestParam(required = false) String note,
                                RedirectAttributes redirectAttributes) {
        try {
            inboundService.rejectInboundReceipt(id,note);

            redirectAttributes.addFlashAttribute("success", "Đã từ chối phiếu nhập kho.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/manager/inbound/approval-list"; // Thống nhất đường dẫn redirect
    }

    // ==========================================
    // 2. API METHODS (Cho Máy quét JavaScript)
    // ==========================================

    @GetMapping("/api/purchase-orders/{poCode}/items")
    @ResponseBody
    public ResponseEntity<?> getPoItemsForScan(@PathVariable String poCode) {
        try {
            PurchaseOrders po = poRepo.findByPoCode(poCode)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy mã PO: " + poCode));

            List<InboundItemRequestDTO> items = poService.getItemsByPoId(po.getId());

            java.util.Map<String, Object> result = new java.util.HashMap<>();

            result.put("poId", po.getId());
            result.put("supplierId", po.getSupplier() != null ? po.getSupplier().getSupplierId() : null);
            result.put("warehouseId", po.getWarehouse() != null ? po.getWarehouse().getWarehouseId() : null);
            result.put("items", items);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/api/confirm")
    @ResponseBody
    public ResponseEntity<?> createInboundFromScan(@RequestBody InboundRequestDTO dto) {
        try {
            Long userId = securityUtils.getCurrentUserId();
            InboundReceipt saved = inboundService.createInboundReceipt(dto, userId);
            return ResponseEntity.ok("Phiếu nhập kho " + saved.getInboundReceiptCode() + " đã được tạo, chờ phê duyệt.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi lưu phiếu: " + e.getMessage());
        }
    }

    @GetMapping("/api/receipts/{id}/items")
    @ResponseBody
    public ResponseEntity<?> getReceiptItems(@PathVariable Long id) {
        try {
            InboundReceipt receipt = inboundService.getById(id);

            // Đóng gói dữ liệu ra Map để tránh lỗi vòng lặp vô hạn (Infinite Loop) của JPA
            List<java.util.Map<String, Object>> responseList = receipt.getItems().stream().map(item -> {
                java.util.Map<String, Object> map = new java.util.HashMap<>();
                map.put("expectedQty", item.getExpectedQty());
                map.put("actualQty", item.getActualQty());
                map.put("putawayLocationId", item.getPutawayLocationId());

                // Tránh lỗi null product
                if (item.getProduct() != null) {
                    java.util.Map<String, Object> productMap = new java.util.HashMap<>();
                    productMap.put("productName", item.getProduct().getProductName());
                    map.put("product", productMap);
                }

                // Tránh lỗi null batch
                if (item.getBatch() != null) {
                    java.util.Map<String, Object> batchMap = new java.util.HashMap<>();
                    batchMap.put("lotCode", item.getBatch().getLotCode());
                    map.put("batch", batchMap);
                }

                return map;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(responseList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Thêm vào InboundController.java

    @GetMapping("/{id}/print")
    public String printInboundReceipt(@PathVariable Long id, Model model) {
        // Lấy thông tin phiếu nhập từ Service
        InboundReceipt receipt = inboundService.getById(id);

        // Đưa dữ liệu vào Model để Thymeleaf đổ ra HTML
        model.addAttribute("receipt", receipt);

        // Trả về file HTML nằm trong thư mục templates/inbound/print-receipt.html
        return "inboundOutboundTransfer/print-inbound-receipt";
    }
    @GetMapping("/history")
    public String inboundHistory(Model model) {
        // Lấy tất cả phiếu có status khác PENDING (hoặc lấy COMPLETED và REJECTED)
        List<InboundReceipt> historyReceipts = inboundService.getHistoryReceipts();

        model.addAttribute("historyReceipts", historyReceipts);
        return "inboundOutboundTransfer/manager-inbound-history";
    }
}
