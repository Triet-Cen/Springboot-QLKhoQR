package com.tttn.warehouseqr.modules.masterdata.warehouse.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "warehouses")
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "warehouse_id")
    private long warehouseId;

    @Column(name = "warehouse_code",nullable = false,length = 50)
    private String warehouseCode;

    @Column(name = "warehouse_name",nullable = false,length = 255)
    private String warehosueName;

    @Column(name = "warehouse_address",nullable = true,columnDefinition = "TEXT")
    private String warehouseAddress;

    public Warehouse() {
    }

    public Warehouse(long warehouseId, String warehouseCode, String warehosueName, String warehouseAddress) {
        this.warehouseId = warehouseId;
        this.warehouseCode = warehouseCode;
        this.warehosueName = warehosueName;
        this.warehouseAddress = warehouseAddress;
    }

    public long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(long warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getWarehosueName() {
        return warehosueName;
    }

    public void setWarehosueName(String warehosueName) {
        this.warehosueName = warehosueName;
    }

    public String getWarehouseAddress() {
        return warehouseAddress;
    }

    public void setWarehouseAddress(String warehouseAddress) {
        this.warehouseAddress = warehouseAddress;
    }

}
