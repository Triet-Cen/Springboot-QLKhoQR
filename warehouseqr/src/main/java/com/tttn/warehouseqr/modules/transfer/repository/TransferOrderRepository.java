package com.tttn.warehouseqr.modules.transfer.repository;

import com.tttn.warehouseqr.modules.transfer.entity.TransferOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferOrderRepository extends JpaRepository<TransferOrder, Long> {

}
