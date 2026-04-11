package com.tttn.warehouseqr.modules.masterdata.unit.repository;

import com.tttn.warehouseqr.modules.masterdata.unit.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitRepository extends JpaRepository<Unit,Long> {
	boolean existsByUnitNameIgnoreCase(String unitName);

	@Query("""
		SELECT u FROM Unit u
		WHERE (:keyword IS NULL OR :keyword = '')
		   OR LOWER(u.unitName) LIKE LOWER(CONCAT('%', :keyword, '%'))
	""")
	List<Unit> searchByKeyword(@Param("keyword") String keyword);
}
