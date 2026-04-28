
package com.tttn.warehouseqr.modules.stocktake.repository;

import com.tttn.warehouseqr.modules.stocktake.entity.StocktakeSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StocktakeSessionRepository extends JpaRepository<StocktakeSession, Long> {
    List<StocktakeSession> findByWarehouseIdAndStatusIn(Long warehouseId, List<String> statuses);
    Optional<StocktakeSession> findTopByOrderByCreatedAtDesc();

    Optional<StocktakeSession> findTopByWarehouseIdOrderByCreatedAtDesc(Long warehouseId);
}