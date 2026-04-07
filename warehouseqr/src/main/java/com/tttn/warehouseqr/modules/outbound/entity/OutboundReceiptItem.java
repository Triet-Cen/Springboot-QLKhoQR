package com.tttn.warehouseqr.modules.outbound.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "outbound_receipt_items")
public class OutboundReceiptItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "requested_qty")
    private BigDecimal requestedQty;

    @Column(name = "actual_qty")
    private BigDecimal actualQty;

    @Column(name = "outbound_receipt_id")
    private Long outboundReceiptId;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "batch_id")
    private Long batchId;

    @Column(name = "picked_location_id")
    private Long pickedLocationId;
}