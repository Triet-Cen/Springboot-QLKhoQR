package com.tttn.warehouseqr.modules.masterdata.category.repository;

import com.tttn.warehouseqr.modules.masterdata.category.dto.CategoryDTO;
import com.tttn.warehouseqr.modules.masterdata.category.entity.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<ProductCategory,Long> {
    @Query("SELECT new com.tttn.warehouseqr.modules.masterdata.category.dto.CategoryDTO(" +
            "c.categoryId ,c.categoryCode, c.categoryName) " +
            "FROM ProductCategory c " +
            "WHERE (:keyw IS NULL OR :keyw = '' OR c.categoryCode LIKE %:keyw% OR c.categoryName LIKE %:keyw%)")
    Page<CategoryDTO> categoryPage(@Param("keyw") String keyw, Pageable pageable);
}
