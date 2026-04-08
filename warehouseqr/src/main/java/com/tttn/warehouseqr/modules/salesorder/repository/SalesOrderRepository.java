package com.tttn.warehouseqr.modules.salesorder.repository;

import com.tttn.warehouseqr.modules.salesorder.entity.SalesOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {
    Optional<SalesOrder> findBySoCode(String soCode);

}
