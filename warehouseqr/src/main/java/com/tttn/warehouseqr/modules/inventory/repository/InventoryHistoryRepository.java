package com.tttn.warehouseqr.modules.inventory.repository;

import com.tttn.warehouseqr.modules.inventory.entity.InventoryHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryHistoryRepository extends JpaRepository<InventoryHistory, Long> {
}