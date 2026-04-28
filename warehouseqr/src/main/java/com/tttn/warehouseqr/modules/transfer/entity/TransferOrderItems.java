package com.tttn.warehouseqr.modules.transfer.entity;

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
@Entity(name = "transfer_order_items")
public class TransferOrderItems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal qty;

    private Long fromLocationId;

    private Long toLocationId;

    private Long productId;
    private Long batchId;

    private Long toWarehouseId;

    @ManyToOne
    @JoinColumn(name = "transfer_order_id")
    private TransferOrder transferOrder;
}
