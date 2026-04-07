package com.tttn.warehouseqr.modules.masterdata.product.entity;

import com.tttn.warehouseqr.modules.masterdata.supplier.entity.Supplier;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "product_batches")
public class ProductBatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "batch_id")
    private long batchId;

    @Column(name = "lot_code",nullable = false)
    private String lotCode;

    @Column(name = "serial_number")
    private String serialNumber;

    @Column(name = "cost_price")
    private BigDecimal costPrice;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public ProductBatch() {
    }

    public ProductBatch(long batchId, String lotCode, String serialNumber,
                        BigDecimal costPrice, LocalDate expiryDate, Supplier supplier, Product product) {
        this.batchId = batchId;
        this.lotCode = lotCode;
        this.serialNumber = serialNumber;
        this.costPrice = costPrice;
        this.expiryDate = expiryDate;
        this.supplier = supplier;
        this.product = product;
    }

    public long getBatchId() {
        return batchId;
    }

    public void setBatchId(long batchId) {
        this.batchId = batchId;
    }

    public String getLotCode() {
        return lotCode;
    }

    public void setLotCode(String lotCode) {
        this.lotCode = lotCode;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
