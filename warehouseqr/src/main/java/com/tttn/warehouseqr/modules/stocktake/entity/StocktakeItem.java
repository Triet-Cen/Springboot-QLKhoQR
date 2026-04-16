
package com.tttn.warehouseqr.modules.stocktake.entity;

import com.tttn.warehouseqr.modules.masterdata.product.entity.Product;
import com.tttn.warehouseqr.modules.masterdata.warehouse.entity.WarehouseLocation;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "stocktake_items")
@Data
public class StocktakeItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Column(name = "system_qty")
    private BigDecimal systemQty;

    @Column(name = "actual_qty")
    private BigDecimal actualQty;

    @Column(name = "variance_qty")
    private BigDecimal varianceQty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", insertable = false, updatable = false)
    private WarehouseLocation location;

    @Column(name = "batch_id")
    private Long batchId;

    @Column(name = "location_id")
    private Long locationId;

    @Column(name = "product_id")
    private Long productId;
}