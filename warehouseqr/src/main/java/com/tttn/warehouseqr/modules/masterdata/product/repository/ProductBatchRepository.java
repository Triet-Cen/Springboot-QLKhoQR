package com.tttn.warehouseqr.modules.masterdata.product.repository;

import com.tttn.warehouseqr.modules.masterdata.product.dto.ProductQrDTO;
import com.tttn.warehouseqr.modules.masterdata.product.entity.ProductBatch;
import org.hibernate.engine.jdbc.batch.spi.Batch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
