package com.tttn.warehouseqr.modules.masterdata.warehouse.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "warehouse_zones")
public class WarehouseZone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "zone_id")
    private Long zoneId;

    @Column(name = "zone_code", unique = true, nullable = false, length = 50)
    private String zoneCode;

    @Column(name = "zone_name", length = 255)
    private String zoneName;

    // Quan hệ ManyToOne: Nhiều Zone thuộc về 1 Warehouse
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouses", nullable = false)
    private Warehouse warehouse;

    public WarehouseZone() {
    }

    public WarehouseZone(Long zoneId, String zoneCode, String zoneName, Warehouse warehouse) {
        this.zoneId = zoneId;
        this.zoneCode = zoneCode;
        this.zoneName = zoneName;
        this.warehouse = warehouse;
    }

    public Long getZoneId() {
        return zoneId;
    }

    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    public String getZoneCode() {
        return zoneCode;
    }

    public void setZoneCode(String zoneCode) {
        this.zoneCode = zoneCode;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }
}
