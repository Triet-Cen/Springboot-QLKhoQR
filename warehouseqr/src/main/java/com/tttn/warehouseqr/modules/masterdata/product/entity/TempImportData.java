package com.tttn.warehouseqr.modules.masterdata.product.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "temp_import_data")
@Data
public class TempImportData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sku;
    @Column(name = "product_name")
    private String productName;
    private String description;
    @Column(name = "min_stock")
    private String minStock;
    @Column(name = "category_id")
    private String categoryId;
    @Column(name = "unit_id")
    private String unitId;
    @Column(name = "batch_code")
    private String batchCode;
    @Column(name = "serial_num")
    private String serialNum;
    @Column(name = "cost_price")
    private String costPrice;
    @Column(name = "expiry_date")
    private String expiryDate;
    @Column(name = "supplier_id")
    private String supplierId;
    private String quantity;
    @Column(name = "location_code")
    private String locationCode;
    @Column(name = "warehouse_id")
    private String warehouseId;

    @Column(name = "validation_status")
    private String validationStatus;
    @Column(name = "validation_message")
    private String validationMessage;
    @Column(name = "import_session_id")
    private String importSessionId;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
