package com.tttn.warehouseqr.modules.purchase.entity;

import com.tttn.warehouseqr.modules.masterdata.product.entity.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "purchase_order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "po_id")
    private PurchaseOrders purchaseOrders;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "ordered_qty", precision = 15, scale = 2)
    private BigDecimal orderedQty; // Số lượng đặt mua

    @Column(name = "received_qty", precision = 15, scale = 2)
    private BigDecimal receivedQty; // Số lượng đã về kho

    @Column(name = "unit_price", precision = 19, scale = 4)
    private BigDecimal unitPrice;

    @Column(name = "batch_id")
    private Long batchId;
}