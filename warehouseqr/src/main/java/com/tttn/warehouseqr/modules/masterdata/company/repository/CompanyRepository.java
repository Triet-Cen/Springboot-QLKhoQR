package com.tttn.warehouseqr.modules.masterdata.company.repository;

import com.tttn.warehouseqr.modules.masterdata.company.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
	@Query("""
		SELECT c FROM Company c
		WHERE (:keyword IS NULL OR :keyword = '')
		   OR LOWER(c.companyName) LIKE LOWER(CONCAT('%', :keyword, '%'))
		   OR LOWER(COALESCE(c.taxCode, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
		   OR LOWER(COALESCE(c.phone, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
		   OR LOWER(COALESCE(c.email, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
		   OR LOWER(COALESCE(c.address, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
	""")
	List<Company> searchByKeyword(@Param("keyword") String keyword);
}

