package com.tttn.warehouseqr.modules.transfer.controller;

import com.tttn.warehouseqr.common.util.SecurityUtils;
import com.tttn.warehouseqr.modules.masterdata.product.repository.ProductBatchRepository;
import com.tttn.warehouseqr.modules.masterdata.product.repository.ProductRepository;
import com.tttn.warehouseqr.modules.masterdata.warehouse.entity.Warehouse;
import com.tttn.warehouseqr.modules.masterdata.warehouse.repository.WarehouseLocationRepository;
import com.tttn.warehouseqr.modules.masterdata.warehouse.services.imp.WarehouseServiceImpl;
import com.tttn.warehouseqr.modules.transfer.entity.TransferOrder;
import com.tttn.warehouseqr.modules.transfer.entity.TransferOrderItems;
import com.tttn.warehouseqr.modules.transfer.repository.TransferOrderItemRepository;
import com.tttn.warehouseqr.modules.transfer.repository.TransferOrderRepository;
import com.tttn.warehouseqr.modules.transfer.services.imp.TransferOrderServicesImp;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/manager/transfer")
public class TransferManagerController {

    private final TransferOrderServicesImp transferOrderServices;

    private final TransferOrderRepository transferOrderRepository;

    private final WarehouseServiceImpl warehouseServiceImpl;

    private final TransferOrderItemRepository itemRepository;
    private final ProductRepository productRepository;
    private final ProductBatchRepository batchRepository;
    private final WarehouseLocationRepository locationRepository;

    public TransferManagerController(TransferOrderServicesImp transferOrderServices, TransferOrderRepository transferOrderRepository, WarehouseServiceImpl warehouseServiceImpl, TransferOrderRepository itemRepository, TransferOrderItemRepository itemRepository1, ProductRepository productRepository, ProductBatchRepository batchRepository, WarehouseLocationRepository locationRepository) {
        this.transferOrderServices = transferOrderServices;
        this.transferOrderRepository = transferOrderRepository;
        this.warehouseServiceImpl = warehouseServiceImpl;
        this.itemRepository = itemRepository1;
        this.productRepository = productRepository;
        this.batchRepository = batchRepository;
        this.locationRepository = locationRepository;
    }


    // =====================================================================
    // 1. TRẢ VỀ GIAO DIỆN DANH SÁCH CHỜ DUYỆT
    // =====================================================================
    @GetMapping("/approval-list")
    public String showApprovalList(Model model) {

        List<TransferOrder> pendingList = transferOrderRepository.findByStatusOrderByTransferDateDesc("PENDING_APPROVAL");
        model.addAttribute("pendingTransfers", pendingList);

        return "inboundOutboundTransfer/manager-transfer-approval"; // Trả về file HTML danh sách duyệt
    }

    // =====================================================================
    // 2. API PHÊ DUYỆT (APPROVE)
    // =====================================================================
    @PostMapping("/api/{id}/approve")
    @ResponseBody // Bắt buộc có để trả về dữ liệu JSON thay vì trả về trang web
    public ResponseEntity<?> approveTransfer(@PathVariable("id") Long transferOrderId) {
        try {
            // Lấy ID của Quản lý đang đăng nhập thao tác
            Long approverId = SecurityUtils.getCurrentUserId();

            // Gọi Service thực hiện Trừ/Cộng kho & Tạo Phiếu Xuất
            transferOrderServices.approveTransferOrder(transferOrderId, approverId);

            return ResponseEntity.ok(Map.of("message", "Đã phê duyệt lệnh điều chuyển và xuất kho thành công!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // =====================================================================
    // 3. API TỪ CHỐI (REJECT)
    // =====================================================================
    @PostMapping("/api/{id}/reject")
    @ResponseBody
    public ResponseEntity<?> rejectTransfer(
            @PathVariable("id") Long transferOrderId,
            @RequestBody(required = false) Map<String, String> payload) {
        try {
            Long approverId = SecurityUtils.getCurrentUserId();

            // Lấy lý do từ chối từ Frontend gửi lên (nếu có)
            String reason = (payload != null && payload.containsKey("reason"))
                    ? payload.get("reason")
                    : "Từ chối không nêu lý do";

            // Gọi Service hủy đơn
            transferOrderServices.rejectTransferOrder(transferOrderId, reason, approverId);

            return ResponseEntity.ok(Map.of("message", "Đã từ chối lệnh điều chuyển kho!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // =====================================================================
    // 4. TRẢ VỀ GIAO DIỆN LỊCH SỬ ĐIỀU CHUYỂN
    // =====================================================================
    @GetMapping("/history")
    public String showTransferHistory(Model model) {
        // Lấy các đơn đã DUYỆT (COMPLETED) hoặc TỪ CHỐI (REJECTED)
        List<TransferOrder> historyList = transferOrderRepository.findByStatusInOrderByTransferDateDesc(
                java.util.List.of("COMPLETED", "REJECTED")
        );
        Map<Long,String> whMap = warehouseServiceImpl.getAllWarehouse()
                        .stream()
                        .collect(Collectors.toMap(Warehouse::getWarehouseId,Warehouse::getWarehouseName));

        model.addAttribute("whMap",whMap);
        model.addAttribute("transferHistory", historyList);

        return "inboundOutboundTransfer/transfer-history"; // Trả về file HTML lịch sử
    }

    // =====================================================================
    // 5. API LẤY CHI TIẾT ĐỂ XEM & IN PHIẾU
    // =====================================================================
    @GetMapping("/api/{id}/details")
    @ResponseBody
    public ResponseEntity<?> getTransferDetails(@PathVariable("id") Long id) {
        try {
            TransferOrder order = transferOrderRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy lệnh chuyển kho!"));

            List<TransferOrderItems> items = itemRepository.findByTransferOrder_Id(id);

            // Đóng gói Header (Thông tin chung)
            Map<String, Object> response = new HashMap<>();
            response.put("transferCode", order.getTransferCode());
            response.put("transferDate", order.getTransferDate().toString());
            response.put("creator", order.getCreator() != null ? order.getCreator().getFullName() : "Admin");
            response.put("status", order.getStatus());

            // Đóng gói danh sách Hàng hóa (Dịch ID sang Tên)
            List<Map<String, Object>> itemDetails = new ArrayList<>();
            for (TransferOrderItems item : items) {
                Map<String, Object> detail = new HashMap<>();
                detail.put("qty", item.getQty());

                // Tìm Tên Sản phẩm
                productRepository.findById(item.getProductId())
                        .ifPresent(p -> detail.put("productName", p.getProductName()));

                // Tìm Mã Lô
                if(item.getBatchId() != null) {
                    batchRepository.findById(item.getBatchId())
                            .ifPresent(b -> detail.put("lotCode", b.getLotCode()));
                } else {
                    detail.put("lotCode", "Mặc định");
                }

                // Tìm Mã Kệ xuất và nhập
                locationRepository.findById(item.getFromLocationId())
                        .ifPresent(l -> detail.put("fromLocation", l.getLocationCode()));
                locationRepository.findById(item.getToLocationId())
                        .ifPresent(l -> detail.put("toLocation", l.getLocationCode()));

                itemDetails.add(detail);
            }

            response.put("items", itemDetails);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
