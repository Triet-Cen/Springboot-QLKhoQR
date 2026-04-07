package com.tttn.warehouseqr.modules.masterdata.product.dto;

import java.time.LocalDate;

public class ProductQrDTO {
    private Long productId;
    private Long batchId;
    private String sku;
    private String productName;
    private String lotCode;
    private LocalDate expiryDate;
    private boolean hasQr;       // Trạng thái: Đã tạo QR hay chưa?
    private String qrBase64;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getLotCode() {
        return lotCode;
    }

    public void setLotCode(String lotCode) {
        this.lotCode = lotCode;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isHasQr() {
        return hasQr;
    }

    public void setHasQr(boolean hasQr) {
        this.hasQr = hasQr;
    }

    public String getQrBase64() {
        return qrBase64;
    }

    public void setQrBase64(String qrBase64) {
        this.qrBase64 = qrBase64;
    }

    public ProductQrDTO() {
    }

    public ProductQrDTO(Long productId, Long batchId, String sku, String productName, String lotCode, LocalDate expiryDate, boolean hasQr, String qrBase64) {
        this.productId = productId;
        this.batchId = batchId;
        this.sku = sku;
        this.productName = productName;
        this.lotCode = lotCode;
        this.expiryDate = expiryDate;
        this.hasQr = hasQr;
        this.qrBase64 = qrBase64;
    }
}
