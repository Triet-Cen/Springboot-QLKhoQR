package com.tttn.warehouseqr.modules.outbound.repository;

import com.tttn.warehouseqr.modules.outbound.entity.OutboundIdempotencyRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OutboundIdempotencyRepository extends JpaRepository<OutboundIdempotencyRecord, Long> {

    Optional<OutboundIdempotencyRecord> findByIdempotencyKey(String idempotencyKey);

    @Modifying
    @Query(value = "INSERT IGNORE INTO outbound_idempotency_records (idempotency_key, status, created_at, updated_at) " +
            "VALUES (:idempotencyKey, 'PROCESSING', NOW(), NOW())", nativeQuery = true)
    int claimKey(@Param("idempotencyKey") String idempotencyKey);

    @Modifying
    @Query(value = "UPDATE outbound_idempotency_records " +
            "SET status = :status, outbound_receipt_id = :receiptId, updated_at = NOW() " +
            "WHERE idempotency_key = :idempotencyKey", nativeQuery = true)
    int markCompleted(@Param("idempotencyKey") String idempotencyKey,
                      @Param("status") String status,
                      @Param("receiptId") Long receiptId);
}

