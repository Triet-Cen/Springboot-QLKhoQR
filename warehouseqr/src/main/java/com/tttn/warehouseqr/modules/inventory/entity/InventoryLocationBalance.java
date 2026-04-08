package com.tttn.warehouseqr.modules.inventory.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "inventory_location_balances")
public class InventoryLocationBalance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;

    @Column(name = "location_id", nullable = false)
    private Long locationId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "batch_id")
    private Long batchId;

    @Column(name = "qty", precision = 15, scale = 2)
    private BigDecimal qty;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "status")
    private String status;
}