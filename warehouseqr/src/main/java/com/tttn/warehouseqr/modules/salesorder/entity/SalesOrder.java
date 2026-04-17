package com.tttn.warehouseqr.modules.salesorder.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sales_orders")
public class SalesOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "so_code", unique = true, nullable = false, length = 50)
    private String soCode;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "status", length = 50)
    private String status = "DRAFT"; // DRAFT (Nháp), PENDING (Đã duyệt), SHIPPED, CANCELLED

    @Column(name = "total_amount", precision = 19, scale = 4)
    private BigDecimal totalAmount;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // 👉 MỚI THÊM: Trạng thái thanh toán (UNPAID / PAID)
    @Column(name = "payment_status", length = 50)
    private String paymentStatus = "UNPAID";

    // 👉 MỚI THÊM: Phương thức thanh toán (COD / TRANSFER)
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "payment_transaction_code", length = 100)
    private String paymentTransactionCode;

    @Column(name = "payment_note", length = 500)
    private String paymentNote;

    @Column(name = "payment_confirmed_at")
    private LocalDateTime paymentConfirmedAt;

    @Column(name = "payment_confirmed_by")
    private Long paymentConfirmedBy;

    @PrePersist
    protected void onCreate() {
        if (status == null || status.isBlank()) {
            status = "DRAFT";
        }
        if (paymentStatus == null || paymentStatus.isBlank()) {
            paymentStatus = "UNPAID";
        }
    }

    // Quan hệ song hướng để lấy nhanh danh sách sản phẩm khi quét mã đơn hàng
    @OneToMany(mappedBy = "salesOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SalesOrderItem> items;
}