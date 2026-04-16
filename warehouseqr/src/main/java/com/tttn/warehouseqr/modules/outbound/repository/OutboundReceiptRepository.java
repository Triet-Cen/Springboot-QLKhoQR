package com.tttn.warehouseqr.modules.outbound.repository;

import com.tttn.warehouseqr.modules.outbound.entity.OutboundReceipt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface OutboundReceiptRepository extends JpaRepository<OutboundReceipt, Long> {

    // CHÚ Ý: Bắt buộc phải có dòng @Query này để Spring không bị lỗi "No property"
    @Query("SELECT r FROM OutboundReceipt r WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR LOWER(r.outboundReceiptCode) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:startDate IS NULL OR r.createdAt >= :startDate) " +
            "AND (:endDate IS NULL OR r.createdAt <= :endDate)")
    Page<OutboundReceipt> searchHistory(@Param("keyword") String keyword,
                                        @Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate,
                                        Pageable pageable);
}