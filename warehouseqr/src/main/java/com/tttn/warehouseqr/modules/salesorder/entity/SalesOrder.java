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
    private String status; // PENDING, PICKING, SHIPPED, CANCELLED

    @Column(name = "total_amount", precision = 19, scale = 4)
    private BigDecimal totalAmount;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Quan hệ song hướng để lấy nhanh danh sách sản phẩm khi quét mã đơn hàng
    @OneToMany(mappedBy = "salesOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SalesOrderItem> items;
}
