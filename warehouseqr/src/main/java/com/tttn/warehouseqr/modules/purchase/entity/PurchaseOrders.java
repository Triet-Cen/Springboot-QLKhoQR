package com.tttn.warehouseqr.modules.purchase.entity;

import com.tttn.warehouseqr.modules.auth.entity.User;
import com.tttn.warehouseqr.modules.masterdata.supplier.entity.Supplier;
import com.tttn.warehouseqr.modules.masterdata.warehouse.entity.Warehouse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "purchase_orders")
public class PurchaseOrders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String poCode;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    private LocalDateTime orderDate;

    private LocalDateTime expectedDeliveryDate;

    private BigDecimal totalAmount;

    private String status;

    private String notes;

    @OneToMany(mappedBy = "purchaseOrders", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseOrderItem> items = new ArrayList<>();

    // --- THÊM HÀM NÀY VÀO ĐỂ FIX LỖI ---
    public void addItem(PurchaseOrderItem item) {
        if (items == null) {
            items = new ArrayList<>();
        }
        items.add(item);
        // "this" chính là đối tượng PurchaseOrders hiện tại
        item.setPurchaseOrders(this);
    }
}
