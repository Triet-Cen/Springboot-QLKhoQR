package com.tttn.warehouseqr.modules.masterdata.warehouse.entity;

import com.tttn.warehouseqr.modules.inventory.entity.InventoryLocationBalance;
import jakarta.persistence.*;

import java.util.List;

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

    @Column(name = "aisle_code", length = 50)
    private String aisleCode;

    @Column(name = "rack_code", length = 50)
    private String rackCode;

    @Column(name = "bin_code", length = 50)
    private String binCode;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "used_capacity")
    private Integer usedCapacity;

    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "locationId", fetch = FetchType.EAGER)
    private List<InventoryLocationBalance> balances;

    public WarehouseLocation() {
    }

    public WarehouseLocation(long locationId, String locationCode, Warehouse warehouses, WarehouseZone zone, Long qrCodeId, String aisleCode, String rackCode, String binCode, Integer capacity, Integer usedCapacity, String status, String description, List<InventoryLocationBalance> balances) {
        this.locationId = locationId;
        this.locationCode = locationCode;
        this.warehouses = warehouses;
        this.zone = zone;
        this.qrCodeId = qrCodeId;
        this.aisleCode = aisleCode;
        this.rackCode = rackCode;
        this.binCode = binCode;
        this.capacity = capacity;
        this.usedCapacity = usedCapacity;
        this.status = status;
        this.description = description;
        this.balances = balances;
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

    public String getAisleCode() {
        return aisleCode;
    }

    public void setAisleCode(String aisleCode) {
        this.aisleCode = aisleCode;
    }

    public String getRackCode() {
        return rackCode;
    }

    public void setRackCode(String rackCode) {
        this.rackCode = rackCode;
    }

    public String getBinCode() {
        return binCode;
    }

    public void setBinCode(String binCode) {
        this.binCode = binCode;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    @Transient
    public Integer getUsedCapacity() {
        if (balances == null || balances.isEmpty()){
            return 0;
        }

        return balances.stream()
                .mapToInt(b -> b.getQty() != null ? b.getQty().intValue() : 0)
                .sum();
    }

    public void setUsedCapacity(Integer usedCapacity) {
        this.usedCapacity = usedCapacity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<InventoryLocationBalance> getBalances() {
        return balances;
    }

    public void setBalances(List<InventoryLocationBalance> balances) {
        this.balances = balances;
    }
}
