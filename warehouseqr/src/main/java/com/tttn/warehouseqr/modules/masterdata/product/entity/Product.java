package com.tttn.warehouseqr.modules.masterdata.product.entity;

import com.tttn.warehouseqr.modules.masterdata.category.entity.ProductCategory;
import com.tttn.warehouseqr.modules.masterdata.unit.entity.Unit;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private long product_id;

    @Column(name = "sku",nullable = false,length = 100)
    private String sku;

    @Column(name = "product_name",nullable = false,length = 255)
    private String productName;

    @Column(name = "description",columnDefinition = "TEXT")
    private String description;

    @Column(name = "min_stock", precision = 10, scale = 2)
    private BigDecimal minStock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private ProductCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id")
    private Unit unit;

    public Product() {
    }

    public Product(long product_id, String sku, String productName, String description, BigDecimal minStock, ProductCategory category, Unit unit) {
        this.product_id = product_id;
        this.sku = sku;
        this.productName = productName;
        this.description = description;
        this.minStock = minStock;
        this.category = category;
        this.unit = unit;
    }

    public long getProduct_id() {
        return product_id;
    }

    public void setProduct_id(long product_id) {
        this.product_id = product_id;
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

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }
}
