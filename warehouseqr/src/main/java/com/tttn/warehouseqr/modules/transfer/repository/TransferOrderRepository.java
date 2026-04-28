package com.tttn.warehouseqr.modules.transfer.repository;

import com.tttn.warehouseqr.modules.transfer.entity.TransferOrder;
import com.tttn.warehouseqr.modules.transfer.entity.TransferOrderItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransferOrderRepository extends JpaRepository<TransferOrder, Long> {


    List<TransferOrder> findByStatusInOrderByTransferDateDesc(List<String> statuses);

    List<TransferOrder> findByStatusOrderByTransferDateDesc(String status);

}
