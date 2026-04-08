package com.tttn.warehouseqr.modules.inbound.entity;

import com.tttn.warehouseqr.modules.masterdata.product.entity.Product;
import com.tttn.warehouseqr.modules.masterdata.product.entity.ProductBatch;
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
@Table(name = "inbound_receipt_items")
public class InboundReceiptItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "expected_qty", nullable = false, precision = 15, scale = 2)
    private BigDecimal expectedQty;

    @Column(name = "actual_qty", precision = 15, scale = 2)
    private BigDecimal actualQty;

    @Column(name = "import_price", precision = 15, scale = 2)
    private BigDecimal importPrice;

    @ManyToOne
    @JoinColumn(name = "inbound_receipt_id", nullable = false)
    private InboundReceipt inboundReceipt;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "batch_id")
    private ProductBatch batch;

    @Column(name = "putaway_location_id")
    private Long putawayLocationId;


}
