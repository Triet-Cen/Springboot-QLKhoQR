package com.tttn.warehouseqr.modules.masterdata.product.repository;

import com.tttn.warehouseqr.modules.inventory.dto.InventoryDetailDto;
import com.tttn.warehouseqr.modules.inventory.dto.InventoryItemDto;
import com.tttn.warehouseqr.modules.masterdata.product.entity.Product;
import com.tttn.warehouseqr.modules.stocktake.dto.ExpiryWarningDto;
import com.tttn.warehouseqr.modules.stocktake.dto.LowStockDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Product findBySku(String sku);

    @Query("SELECT p FROM Product p WHERE p.sku = :sku")
    Optional<Product> findProductBySku(@Param("sku") String sku);

    
    @Query(value = "SELECT p FROM Product p " +
            "WHERE (:keyw IS NULL OR p.productName LIKE CONCAT('%', :keyw, '%')) " +
            "AND (:categoryId = 0 OR p.category.categoryId = :categoryId) ")
    Page<Product> getProducPageCustom(@Param("keyw") String keyw, @Param("categoryId") long categoryId, Pageable page);


    //Báo cáo tồn kho
    // Báo cáo tồn kho
    @Query("SELECT new com.tttn.warehouseqr.modules.inventory.dto.InventoryItemDto(" +
            "p.product_id, " + // BỔ SUNG DÒNG NÀY Ở ĐẦU
            "p.sku, " +
            "p.productName, " +
            "c.categoryName, " +
            "COALESCE(SUM(ilb.qty), 0), " +
            "COALESCE(SUM(ilb.qty * COALESCE(pb.costPrice, 0)), 0), " +
            "CASE WHEN COALESCE(SUM(ilb.qty), 0) <= COALESCE(p.minStock, 0) THEN true ELSE false END) " +
            "FROM Product p " +
            "LEFT JOIN p.category c " +
            "LEFT JOIN InventoryLocationBalance ilb ON p.product_id = ilb.productId " +
            "AND (:warehouseId IS NULL OR ilb.warehouseId = :warehouseId) " +
            "LEFT JOIN ProductBatch pb ON ilb.batchId = pb.batchId " +
            "WHERE (:keyword IS NULL OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "GROUP BY p.product_id, p.sku, p.productName, c.categoryName, p.minStock " +
            "HAVING COUNT(ilb.id) > 0")
    List<InventoryItemDto> getInventoryReport(@Param("keyword") String keyword, @Param("warehouseId") Long warehouseId);


    // cảnh báo tồn kho thấp
    @Query("SELECT new com.tttn.warehouseqr.modules.stocktake.dto.LowStockDto(" +
            "p.sku, p.productName, COALESCE(SUM(ilb.qty), 0), p.minStock) " +
            "FROM Product p " +
            "LEFT JOIN InventoryLocationBalance ilb ON p.product_id = ilb.productId AND ilb.warehouseId = :warehouseId " +
            "GROUP BY p.product_id, p.sku, p.productName, p.minStock " +
            "HAVING COALESCE(SUM(ilb.qty), 0) <= p.minStock")
    List<LowStockDto> findLowStockByWarehouse(@Param("warehouseId") Long warehouseId);


    //Chi tiết sản phẩm trong tồn kho
    @Query("SELECT new com.tttn.warehouseqr.modules.inventory.dto.InventoryDetailDto(" +
            "wl.locationCode, pb.lotCode, ilb.qty) " +
            "FROM InventoryLocationBalance ilb " +
            "JOIN WarehouseLocation wl ON ilb.locationId = wl.locationId " + // Đã sửa location_id thành locationId
            "LEFT JOIN ProductBatch pb ON ilb.batchId = pb.batchId " +
            "WHERE ilb.productId = :productId AND ilb.warehouseId = :warehouseId")
    List<InventoryDetailDto> getProductInventoryDetails(@Param("productId") Long productId, @Param("warehouseId") Long warehouseId);

    //Ham nay de query cac product bi mo coi du lieu
    @Query("SELECT p FROM Product p " +
            "INNER JOIN p.category c " +
            "INNER JOIN p.unit u " +
            "WHERE (:keyw IS NULL OR p.productName LIKE %:keyw% OR p.sku LIKE %:keyw%)" +
            "ORDER BY p.product_id ASC")
    Page<Product> findValidProducts(@Param("keyw") String keyw, Pageable pageable);
}