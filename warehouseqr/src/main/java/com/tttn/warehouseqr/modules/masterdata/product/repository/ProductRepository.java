package com.tttn.warehouseqr.modules.masterdata.product.repository;

import com.tttn.warehouseqr.modules.masterdata.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long>{
    Product findBySku(String sku);
    @Query(value = "SELECT p FROM Product p " +
            "WHERE (:keyw IS NULL OR p.productName LIKE CONCAT('%', :keyw, '%')) " +
            "AND (:categoryId = 0 OR p.category.categoryId = :categoryId) ")
    Page<Product> getProducPageCustom(@Param("keyw") String keyw, @Param("categoryId") long categoryId, Pageable page);

//    @Query(value = "SELECT COUNT(p.product_id) " +
//            "FROM Product p " +
//            "WHERE (:keyw IS NULL OR p.productName LIKE CONCAT('%', :keyw, '%')) " +
//            "AND (:categoryId = 0 OR p.category.categoryId = :categoryId)",nativeQuery = true)
//    long CountTotalElements(@Param("keyw") String keyw, @Param("categoryId") long categoryId);
}
