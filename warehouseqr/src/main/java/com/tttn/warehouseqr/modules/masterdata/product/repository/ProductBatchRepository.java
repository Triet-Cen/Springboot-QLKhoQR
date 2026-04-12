package com.tttn.warehouseqr.modules.masterdata.product.repository;

import com.tttn.warehouseqr.modules.masterdata.product.dto.ProductQrDTO;
import com.tttn.warehouseqr.modules.masterdata.product.entity.ProductBatch;
import com.tttn.warehouseqr.modules.stocktake.dto.ExpiryWarningDto;
import org.hibernate.engine.jdbc.batch.spi.Batch;
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
public interface ProductBatchRepository extends JpaRepository<ProductBatch, Long> {
    @Query("SELECT new com.tttn.warehouseqr.modules.masterdata.product.dto.ProductQrDTO(" +
            "p.product_id, b.batchId, p.sku, p.productName, b.lotCode, b.expiryDate, " +
            "CASE WHEN q.qrCodeId IS NOT NULL THEN true ELSE false END, q.imgPath) " +
            "FROM ProductBatch b " +
            "JOIN b.product p " +
            "LEFT JOIN QrCode q ON q.referenceId = b.batchId AND q.referenceType = 'BATCH' " +
            "WHERE (:keyw IS NULL OR :keyw = '' OR p.sku LIKE %:keyw% OR p.productName LIKE %:keyw% OR b.lotCode LIKE %:keyw%) " +
            "AND (:categoryId = 0 OR p.category.categoryId = :categoryId)")
    Page<ProductQrDTO> searchBatchesWithQr(@Param("keyw") String keyw,@Param("categoryId") long categoryId, Pageable pageable);

    @Query("SELECT pb FROM ProductBatch pb WHERE pb.lotCode = :lotCode AND pb.product.product_id = :productId")
    Optional<ProductBatch> findByLotCodeAndProductProduct_id(String lotCode, long productId);


    // cảnh báo hết hsd
    @Query("SELECT new com.tttn.warehouseqr.modules.stocktake.dto.ExpiryWarningDto(" +
            "p.sku, p.productName, pb.lotCode, pb.expiryDate) " +
            "FROM ProductBatch pb " +
            "JOIN pb.product p " +
            "WHERE pb.expiryDate BETWEEN :start AND :end " +
            "AND EXISTS (SELECT 1 FROM InventoryLocationBalance ilb " +
            "            WHERE ilb.batchId = pb.batchId " +
            "            AND ilb.warehouseId = :warehouseId " +
            "            AND ilb.qty > 0)")
    List<ExpiryWarningDto> findExpiringBatches(@Param("warehouseId") Long warehouseId,
                                               @Param("start") LocalDate start,
                                               @Param("end") LocalDate end);
}
