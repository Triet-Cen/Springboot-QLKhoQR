package com.tttn.warehouseqr.modules.masterdata.supplier.repository;

import com.tttn.warehouseqr.modules.masterdata.supplier.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    Optional<Supplier> findBySupplierCode(String supplierCode);

    boolean existsBySupplierCode(String supplierCode);

    @Query("""
        SELECT s FROM Supplier s
        WHERE (:keyword IS NULL OR :keyword = '')
           OR LOWER(s.supplierCode) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(s.supplierName) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(COALESCE(s.contactPerson, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(COALESCE(s.phone, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    List<Supplier> searchByKeyWord(@Param("keyword") String keyword);
}