
package com.tttn.warehouseqr.modules.stocktake.repository;

import com.tttn.warehouseqr.modules.stocktake.entity.StocktakeSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StocktakeSessionRepository extends JpaRepository<StocktakeSession, Long> {
}