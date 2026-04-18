package com.tttn.warehouseqr.modules.masterdata.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class ProductDTO {
    @NotBlank(message = "SKU không được rỗng!")
    private String sku;
    @NotBlank(message = "Tên sản phẩm không được rỗng!")
    private String productName;
    @NotBlank(message = "Mô tả không được rỗng!")
    private String description;
    @NotNull(message = "Số lượng tối thiểu không được rỗng!")
    @Min(value = 0,message = "Số lượng tối thểu phải lớn hơn 0!")
    private BigDecimal minStock;
    @NotNull(message = "Danh mục khồng được rỗng!")
    private long categoryId;
    @NotNull(message = "Đơn vị tính khồng được rỗng!")
    private long unitId;

    public ProductDTO() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getMinStock() {
        return minStock;
    }

    public void setMinStock(BigDecimal minStock) {
        this.minStock = minStock;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public long getUnitId() {
        return unitId;
    }

    public void setUnitId(long unitId) {
        this.unitId = unitId;
    }
}
