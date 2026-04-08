package com.tttn.warehouseqr.modules.outbound.repository;

import com.tttn.warehouseqr.modules.outbound.entity.OutboundReceipt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboundReceiptRepository extends JpaRepository<OutboundReceipt, Integer> {
}
