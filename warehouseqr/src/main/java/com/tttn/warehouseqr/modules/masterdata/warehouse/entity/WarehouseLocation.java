package com.tttn.warehouseqr.modules.masterdata.warehouse.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "warehouse_locations")
public class WarehouseLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    private long locationId;

    @Column(name = "location_code",nullable = false,length = 50)
    private String locationCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id",nullable = false)
    private Warehouse warehouses;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id",nullable = true)
    private WarehouseZone zone;

    @Column(name = "qr_code_id",nullable = true)
    private Long qrCodeId;

    public WarehouseLocation() {
    }

    public WarehouseLocation(long locationId, String locationCode, Warehouse warehouses, WarehouseZone zone, Long qrCodeId) {
        this.locationId = locationId;
        this.locationCode = locationCode;
        this.warehouses = warehouses;
        this.zone = zone;
        this.qrCodeId = qrCodeId;
    }

    public long getLocationId() {
        return locationId;
    }

    public void setLocationId(long locationId) {
        this.locationId = locationId;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public Warehouse getWarehouses() {
        return warehouses;
    }

    public void setWarehouses(Warehouse warehouses) {
        this.warehouses = warehouses;
    }

    public WarehouseZone getZone() {
        return zone;
    }

    public void setZone(WarehouseZone zone) {
        this.zone = zone;
    }

    public Long getQrCodeId() {
        return qrCodeId;
    }

    public void setQrCodeId(Long qrCodeId) {
        this.qrCodeId = qrCodeId;
    }
}
