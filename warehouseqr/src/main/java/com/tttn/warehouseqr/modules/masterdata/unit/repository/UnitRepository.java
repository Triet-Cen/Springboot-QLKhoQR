package com.tttn.warehouseqr.modules.masterdata.unit.repository;

import com.tttn.warehouseqr.modules.masterdata.unit.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitRepository extends JpaRepository<Unit,Long> {
}
