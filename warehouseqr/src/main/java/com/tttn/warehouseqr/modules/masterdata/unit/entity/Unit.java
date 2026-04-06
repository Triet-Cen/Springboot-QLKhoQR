package com.tttn.warehouseqr.modules.masterdata.unit.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "units")
public class Unit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unit_id")
    private long unitId;

    @Column(name = "unit_name",nullable = false,length = 100)
    private String unitName;

    public Unit() {
    }

    public Unit(long unitId, String unitName) {
        this.unitId = unitId;
        this.unitName = unitName;
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
}
