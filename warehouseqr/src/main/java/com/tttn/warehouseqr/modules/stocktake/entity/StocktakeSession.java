
package com.tttn.warehouseqr.modules.stocktake.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "stocktake_sessions")
@Data
public class StocktakeSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_code", unique = true, nullable = false)
    private String sessionCode;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "status")
    private String status; // DRAFT, IN_PROGRESS, COMPLETED, CANCELLED

    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;
}