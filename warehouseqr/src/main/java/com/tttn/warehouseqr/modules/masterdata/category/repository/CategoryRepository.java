package com.tttn.warehouseqr.modules.masterdata.category.repository;

import com.tttn.warehouseqr.modules.masterdata.category.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<ProductCategory,Long> {

}
