package com.tttn.warehouseqr.modules.outbound.controller;

import com.tttn.warehouseqr.modules.salesorder.entity.SalesOrder;
import com.tttn.warehouseqr.modules.salesorder.repository.SalesOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class AccountingController {

    @Autowired
    private SalesOrderRepository salesOrderRepo;

    // 1. Mở giao diện trang Kế toán
    @GetMapping("/accounting/payments")
    public String paymentPage() {
        return "accounting-payment"; // Trỏ tới file HTML ta sẽ tạo ở Bước 2
    }

    @GetMapping("/manager/approvals")
    public String managerApprovalPage() {
        return "manager-approval-station";
    }

    // 2. API lấy danh sách Đơn hàng cho Kế toán xem
    @GetMapping("/api/accounting/orders")
    @ResponseBody
    public ResponseEntity<?> getAllOrdersForAccounting(Authentication authentication) {
        boolean canRollbackPayment = authentication != null
                && authentication.getAuthorities() != null
                && authentication.getAuthorities().stream().anyMatch(a ->
                "ROLE_ADMIN".equals(a.getAuthority()) || "ROLE_MANAGER".equals(a.getAuthority()));

        // Lấy tất cả đơn hàng, đơn mới nhất xếp lên đầu
        List<SalesOrder> orders = salesOrderRepo.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));

        // Map ra DTO mộc để tránh lỗi đệ quy JSON của Spring Boot
        List<Map<String, Object>> result = orders.stream().map(o -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", o.getId());
            map.put("soCode", o.getSoCode());
            map.put("customerName", o.getCustomerName());
            map.put("totalAmount", o.getTotalAmount());
            map.put("status", o.getStatus()); // DRAFT / PENDING
            map.put("paymentStatus", o.getPaymentStatus()); // UNPAID / PAID
            map.put("paymentMethod", o.getPaymentMethod());
            map.put("paymentTransactionCode", o.getPaymentTransactionCode());
            map.put("paymentNote", o.getPaymentNote());
            map.put("paymentConfirmedAt", o.getPaymentConfirmedAt());
            map.put("paymentConfirmedBy", o.getPaymentConfirmedBy());
            map.put("canRollbackPayment", canRollbackPayment);
            map.put("createdAt", o.getCreatedAt());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/manager/orders/pending-approval")
    @ResponseBody
    public ResponseEntity<?> getOrdersForManagerStation() {
        List<SalesOrder> orders = salesOrderRepo.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));

        List<Map<String, Object>> result = orders.stream().map(o -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", o.getId());
            map.put("soCode", o.getSoCode());
            map.put("customerName", o.getCustomerName());
            map.put("totalAmount", o.getTotalAmount());
            map.put("status", o.getStatus());
            map.put("paymentStatus", o.getPaymentStatus());
            map.put("createdAt", o.getCreatedAt());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
}