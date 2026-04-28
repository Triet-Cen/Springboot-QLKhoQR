package com.tttn.warehouseqr.modules.outbound.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "outbound_receipts")
public class OutboundReceipt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "outbound_receipt_code", nullable = false, length = 50)
    private String outboundReceiptCode;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;

    @Column(name = "customer_id")
    private Long customerId;




}
