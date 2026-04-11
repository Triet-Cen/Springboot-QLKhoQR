package com.tttn.warehouseqr.modules.masterdata.customer.repository;

import com.tttn.warehouseqr.modules.masterdata.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    // JpaRepository đã có sẵn hàm findAll() nên bạn không cần viết gì thêm ở đây cả
}