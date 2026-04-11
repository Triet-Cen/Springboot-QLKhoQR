package com.tttn.warehouseqr.modules.masterdata.unit.entity;

import com.tttn.warehouseqr.modules.masterdata.product.entity.Product;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "units")
public class Unit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unit_id")
    private long unitId;

    @Column(name = "unit_name", nullable = false, unique = true, length = 100)
    private String unitName;

    @OneToMany(mappedBy = "unit")
    private List<Product> products;

    public Unit() {
    }

    public Unit(long unitId, String unitName, List<Product> products) {
        this.unitId = unitId;
        this.unitName = unitName;
        this.products = products;
    }

    public long getUnitId() {
        return unitId;
    }

    public void setUnitId(long unitId) {
        this.unitId = unitId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
