package com.tttn.warehouseqr.modules.inbound.repository;

import com.tttn.warehouseqr.modules.inbound.entity.InboundReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface InboundReceiptRepository extends JpaRepository<InboundReceipt,Long> {
    Optional<InboundReceipt> findByInboundReceiptCode(String code);

    List<InboundReceipt> findByPurchaseOrders_Id(Long purchaseOrderId);

    List<InboundReceipt> findByStatusInOrderByCreatedAtDesc(List<String> statuses);

}
