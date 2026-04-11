package com.tttn.warehouseqr.modules.masterdata.customer.repository;

import com.tttn.warehouseqr.modules.masterdata.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByCustomerCode(String customerCode);
    boolean existsByCustomerCode(String customerCode);

    @Query("""
        SELECT c FROM Customer c
        WHERE (:keyword IS NULL OR :keyword = '')
           OR LOWER(c.customerCode) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(c.customerName) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(COALESCE(c.contactPerson, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(COALESCE(c.phone, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    List<Customer> searchByKeyword(@Param("keyword") String keyword);
}

