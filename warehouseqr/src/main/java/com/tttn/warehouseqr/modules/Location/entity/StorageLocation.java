package com.tttn.warehouseqr.modules.Location.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "warehouse_locations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StorageLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    private Long locationId;

    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;

    @Column(name = "zone_id")
    private Long zoneId;

    @Column(name = "location_code", nullable = false, unique = true, length = 100)
    private String locationCode;

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

    @Column(name = "qr_code_id")
    private Long qrCodeId;

    @Column(name = "description")
    private String description;
}