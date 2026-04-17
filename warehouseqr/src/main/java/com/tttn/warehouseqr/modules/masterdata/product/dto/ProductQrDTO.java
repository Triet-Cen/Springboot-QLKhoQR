package com.tttn.warehouseqr.modules.masterdata.product.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;

public class ProductQrDTO {
    @NotNull(message = "Mã sản phẩm không được rỗng!")
    private Long productId;

    @NotNull(message = "Mã lô không được rỗng!")
    private Long batchId;

    @NotBlank(message = "SKU không được rỗng!")
    private String sku;
    @NotBlank(message = "Tên sản phẩm không được rỗng!")
    private String productName;
    @NotBlank(message = "Lot code không được rỗng!")
    private String lotCode;
    @Future(message = "Nhập ngày trong tương lai!")
    private LocalDate expiryDate;
    private Boolean hasQr;       // Trạng thái: Đã tạo QR hay chưa?
    private String qrBase64;
    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0,message = "Số lượng phải lớn hơn 0!")
    private Double quantity;
    @NotBlank(message = "Vị trí không được để trống!")
    private String location;

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

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

    public ProductQrDTO(Long productId, Long batchId, String sku, String productName, String lotCode, LocalDate expiryDate, Boolean hasQr, String qrBase64) {
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
