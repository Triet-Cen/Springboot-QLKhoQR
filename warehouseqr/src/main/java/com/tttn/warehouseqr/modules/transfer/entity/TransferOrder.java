package com.tttn.warehouseqr.modules.transfer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "transfer_orders")
public class TransferOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String transferCode;

    private Long fromWarehouseId;

    private Long toWarehouseId;

    private LocalDateTime transferDate;

    private String status; // PENDING, COMPLETED

    private Long createdBy;

    private Long outboundReceiptId;

    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "transferOrder")
    private List<TransferOrderItems> transferOrderItems;
}
