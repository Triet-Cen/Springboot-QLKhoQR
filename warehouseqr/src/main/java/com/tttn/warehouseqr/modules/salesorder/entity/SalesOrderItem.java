package com.tttn.warehouseqr.modules.salesorder.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sales_order_items")
public class SalesOrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Liên kết ngược lại đơn hàng cha
    @ManyToOne
    @JoinColumn(name = "so_id", nullable = false)
    private SalesOrder salesOrder;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "quantity", nullable = false, precision = 15, scale = 2)
    private BigDecimal quantity; // Số lượng khách đặt

    @Column(name = "shipped_qty", precision = 15, scale = 2)
    private BigDecimal shippedQty = BigDecimal.ZERO; // Số lượng đã thực xuất

    @Column(name = "unit_price", precision = 19, scale = 4)
    private BigDecimal unitPrice;
}
