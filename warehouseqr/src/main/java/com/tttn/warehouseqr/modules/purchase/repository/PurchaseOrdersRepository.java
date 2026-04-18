package com.tttn.warehouseqr.modules.purchase.repository;

import com.tttn.warehouseqr.modules.purchase.entity.PurchaseOrders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseOrdersRepository extends JpaRepository<PurchaseOrders,Long> {
    List<PurchaseOrders> findAllByOrderByIdDesc();

    // Tìm các đơn theo trạng thái (Ví dụ: Chỉ lấy đơn OPEN để hiển thị cho kho)
    List<PurchaseOrders> findByStatus(String status);

    Optional<PurchaseOrders> findByPoCode(String poCode);
}
