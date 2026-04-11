package com.tttn.warehouseqr.modules.transfer.repository;

import com.tttn.warehouseqr.modules.transfer.entity.TransferOrderItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferOrderItemReposiroty extends JpaRepository<TransferOrderItems, Long> {

}
