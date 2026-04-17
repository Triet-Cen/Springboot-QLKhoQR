package com.tttn.warehouseqr.modules.masterdata.warehouse.entity;

import jakarta.persistence.*;

import java.util.List;

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
    private String warehouseName;

    @Column(name = "warehouse_address",nullable = true,columnDefinition = "TEXT")
    private String warehouseAddress;

    @OneToMany(mappedBy = "warehouses", fetch = FetchType.EAGER)
    private List<WarehouseLocation> locations;



    public Warehouse() {
    }

    public Warehouse(long warehouseId, String warehouseCode, String warehouseName, String warehouseAddress) {
        this.warehouseId = warehouseId;
        this.warehouseCode = warehouseCode;
        this.warehouseName = warehouseName;
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

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getWarehouseAddress() {
        return warehouseAddress;
    }

    public void setWarehouseAddress(String warehouseAddress) {
        this.warehouseAddress = warehouseAddress;
    }


    public List<WarehouseLocation> getLocations() { return locations; }
    public void setLocations(List<WarehouseLocation> locations) { this.locations = locations; }

}
