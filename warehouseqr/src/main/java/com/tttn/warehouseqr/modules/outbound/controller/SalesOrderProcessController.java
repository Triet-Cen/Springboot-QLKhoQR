package com.tttn.warehouseqr.modules.outbound.controller;

import com.tttn.warehouseqr.common.util.SecurityUtils;
import com.tttn.warehouseqr.modules.inventory.repository.InventoryLocationBalanceRepository;
import com.tttn.warehouseqr.modules.masterdata.product.entity.Product;
import com.tttn.warehouseqr.modules.masterdata.product.entity.QrCode;
import com.tttn.warehouseqr.modules.masterdata.product.repository.ProductRepository;
import com.tttn.warehouseqr.modules.masterdata.product.repository.QrCodeResipotory;
import com.tttn.warehouseqr.modules.salesorder.entity.SalesOrder;
import com.tttn.warehouseqr.modules.salesorder.entity.SalesOrderItem;
import com.tttn.warehouseqr.modules.salesorder.repository.SalesOrderRepository;
import com.tttn.warehouseqr.utils.QrCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/sales-orders")
public class SalesOrderProcessController {

    private static final String QR_REFERENCE_TYPE_SO_PICK = "SO_PICK";

    @Autowired
    private SalesOrderRepository salesOrderRepo;

    @Autowired
    private QrCodeResipotory qrCodeRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryLocationBalanceRepository balanceRepository;

    // ========================================================
    // 1. DÀNH CHO QUẢN LÝ: API Duyệt Đơn Xuất Kho
    // Frontend gọi API này khi Quản lý bấm nút "Duyệt"
    // ========================================================
    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approveOrder(@PathVariable Long id) {
        try {
            if (!SecurityUtils.hasAnyRole("ADMIN", "MANAGER")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Bạn không có quyền xác nhận đơn ở trạm trưởng."));
            }

            SalesOrder order = salesOrderRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng có ID: " + id));

            if ("SHIPPED".equalsIgnoreCase(order.getStatus())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Đơn này đã xuất kho xong, không thể xác nhận lại."));
            }

            if ("PENDING".equalsIgnoreCase(order.getStatus())) {
                return ResponseEntity.ok(Map.of("message", "Đơn đã ở trạng thái PENDING từ trước."));
            }

            // Đổi trạng thái từ DRAFT sang PENDING
            order.setStatus("PENDING");
            order.setUpdatedAt(LocalDateTime.now());
            salesOrderRepo.save(order);

            return ResponseEntity.ok(Map.of("message", "Trạm trưởng đã xác nhận đơn thành công."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ========================================================
    // 2. DÀNH CHO KẾ TOÁN: API Xác Nhận Đã Thu Tiền
    // Frontend gọi API này khi Kế toán bấm "Xác nhận thu tiền"
    // ========================================================
    @PostMapping("/{id}/confirm-payment")
    public ResponseEntity<?> confirmPayment(@PathVariable Long id,
                                            @RequestParam String method,
                                            @RequestParam(required = false) String transactionCode,
                                            @RequestParam(required = false) String note) {
        try {
            SalesOrder order = salesOrderRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng có ID: " + id));

            String orderStatus = order.getStatus() != null ? order.getStatus().trim().toUpperCase() : "";
            if ("DRAFT".equals(orderStatus) || orderStatus.isBlank()) {
                throw new RuntimeException("Đơn chưa được Trạm trưởng duyệt. Kế toán chưa thể xác nhận thu tiền.");
            }

            if ("TRANSFER".equalsIgnoreCase(method) && (transactionCode == null || transactionCode.isBlank())) {
                throw new RuntimeException("Đơn chuyển khoản bắt buộc phải nhập Mã giao dịch.");
            }

            // Đổi trạng thái từ UNPAID sang PAID và lưu phương thức (COD/TRANSFER)
            order.setPaymentStatus("PAID");
            order.setPaymentMethod(method);
            order.setPaymentTransactionCode(transactionCode != null ? transactionCode.trim() : null);
            order.setPaymentNote(note != null ? note.trim() : null);
            order.setPaymentConfirmedAt(LocalDateTime.now());
            order.setPaymentConfirmedBy(SecurityUtils.getCurrentUserId());
            salesOrderRepo.save(order);

            List<Map<String, Object>> paymentQrs = new ArrayList<>();
            String qrWarning = null;
            try {
                paymentQrs = getOrCreatePaymentQrs(order);
            } catch (Exception qrEx) {
                // Không rollback trạng thái thu tiền nếu QR lỗi để tránh ảnh hưởng nghiệp vụ kế toán.
                qrWarning = "Thu tiền thành công nhưng tạo QR thất bại: " + qrEx.getMessage();
            }

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("message", "Kế toán đã duyệt tiền thành công! Thủ kho có thể lưu Database.");
            response.put("soCode", order.getSoCode());
            response.put("paymentQrs", paymentQrs);
            if (qrWarning != null) {
                response.put("qrWarning", qrWarning);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/payment-qrs")
    public ResponseEntity<?> getPaymentQrs(@PathVariable Long id) {
        try {
            SalesOrder order = salesOrderRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng có ID: " + id));

            List<Map<String, Object>> paymentQrs = getOrCreatePaymentQrs(order);
            return ResponseEntity.ok(Map.of(
                    "soCode", order.getSoCode(),
                    "paymentQrs", paymentQrs
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/rollback-payment")
    public ResponseEntity<?> rollbackPayment(@PathVariable Long id,
                                             @RequestParam(required = false) String reason) {
        try {
            if (!SecurityUtils.hasAnyRole("ADMIN", "MANAGER")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Bạn không có quyền rollback thanh toán."));
            }

            SalesOrder order = salesOrderRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng có ID: " + id));

            if (!"PAID".equalsIgnoreCase(order.getPaymentStatus())) {
                throw new RuntimeException("Chỉ đơn đã thu tiền mới được rollback.");
            }

            order.setPaymentStatus("UNPAID");
            order.setPaymentMethod("TRANSFER");
            order.setPaymentTransactionCode(null);
            order.setPaymentNote(reason != null && !reason.isBlank() ? "ROLLBACK: " + reason.trim() : "ROLLBACK");
            order.setPaymentConfirmedAt(null);
            order.setPaymentConfirmedBy(null);
            salesOrderRepo.save(order);

            return ResponseEntity.ok(Map.of("message", "Đã rollback trạng thái thanh toán thành công."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private List<Map<String, Object>> getOrCreatePaymentQrs(SalesOrder order) {
        List<QrCode> existing = qrCodeRepository.findAllByReferenceIdAndReferenceType(order.getId(), QR_REFERENCE_TYPE_SO_PICK);
        if (!existing.isEmpty()) {
            return existing.stream()
                    .map(this::toQrTicketResponse)
                    .toList();
        }

        if (order.getItems() == null || order.getItems().isEmpty()) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> qrTickets = new ArrayList<>();
        Set<String> deduplicatedPayload = new HashSet<>();

        for (SalesOrderItem orderItem : order.getItems()) {
            BigDecimal orderedQty = orderItem.getQuantity() != null ? orderItem.getQuantity() : BigDecimal.ZERO;
            BigDecimal shippedQty = orderItem.getShippedQty() != null ? orderItem.getShippedQty() : BigDecimal.ZERO;
            BigDecimal requiredQty = orderedQty.subtract(shippedQty);
            if (requiredQty.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            Product product = productRepository.findById(orderItem.getProductId()).orElse(null);
            String sku = product != null ? product.getSku() : ("PID-" + orderItem.getProductId());
            String productName = product != null ? product.getProductName() : "Sản phẩm";

            List<InventoryLocationBalanceRepository.PickingStockProjection> stocks =
                    balanceRepository.findAvailableStockForPicking(orderItem.getProductId());
            String lotCode = stocks.stream()
                    .map(InventoryLocationBalanceRepository.PickingStockProjection::getLotCode)
                    .filter(Objects::nonNull)
                    .filter(s -> !s.isBlank())
                    .findFirst()
                    .orElse("MAC_DINH");

            String payload = sku + "|" + lotCode;
            if (!deduplicatedPayload.add(payload)) {
                continue;
            }

            String base64Image = QrCodeUtil.GenerateQRCodeBase64(payload, 300, 300);

            QrCode qrCode = new QrCode();
            qrCode.setQrContent(payload);
            qrCode.setImgPath(base64Image);
            qrCode.setReferenceType(QR_REFERENCE_TYPE_SO_PICK);
            qrCode.setReferenceId(order.getId());
            qrCode.setPrinted(false);
            qrCode = qrCodeRepository.save(qrCode);

            Map<String, Object> row = toQrTicketResponse(qrCode);
            row.put("sku", sku);
            row.put("productName", productName);
            row.put("lotCode", lotCode);
            row.put("requiredQty", requiredQty);
            qrTickets.add(row);
        }

        return qrTickets;
    }

    private Map<String, Object> toQrTicketResponse(QrCode qrCode) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("qrCodeId", qrCode.getQrCodeId());
        row.put("payload", qrCode.getQrContent());
        row.put("imgPath", qrCode.getImgPath());

        String payload = qrCode.getQrContent() != null ? qrCode.getQrContent() : "";
        String[] parts = payload.split("\\|", 2);
        row.put("sku", parts.length > 0 ? parts[0] : "");
        row.put("lotCode", parts.length > 1 ? parts[1] : "");
        row.put("productName", "Sản phẩm");
        return row;
    }
}

