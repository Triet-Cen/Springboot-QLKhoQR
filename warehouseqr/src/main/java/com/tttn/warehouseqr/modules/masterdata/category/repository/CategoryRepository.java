package com.tttn.warehouseqr.modules.masterdata.category.repository;

import com.tttn.warehouseqr.modules.masterdata.category.entity.ProductCategory;
import com.tttn.warehouseqr.modules.masterdata.supplier.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<ProductCategory,Long> {
    boolean existsBySupplierCode(String supplierCode);
    boolean existsBySupplierId(Long id);
    List<Supplier> search(@Param("keyword") String keyword);

}
