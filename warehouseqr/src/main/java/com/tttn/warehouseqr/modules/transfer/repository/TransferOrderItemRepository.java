package com.tttn.warehouseqr.modules.transfer.repository;

import com.tttn.warehouseqr.modules.transfer.entity.TransferOrderItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransferOrderItemRepository extends JpaRepository<TransferOrderItems, Long> {
    List<TransferOrderItems> findByTransferOrder_Id(Long transferOrderId);

}
