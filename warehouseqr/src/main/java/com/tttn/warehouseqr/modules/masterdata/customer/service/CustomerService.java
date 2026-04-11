package com.tttn.warehouseqr.modules.masterdata.customer.service;

import com.tttn.warehouseqr.modules.masterdata.customer.dto.CustomerCreateRequest;
import com.tttn.warehouseqr.modules.masterdata.customer.dto.CustomerUpdateRequest;
import com.tttn.warehouseqr.modules.masterdata.customer.entity.Customer;

import java.util.List;

public interface CustomerService {
    void createCustomer(CustomerCreateRequest req);
    void updateCustomer(CustomerUpdateRequest req);
    CustomerUpdateRequest getUpdateById(Long id);
    List<Customer> getAllCustomers();
    List<Customer> searchCustomers(String keyword);
    void deleteCustomer(Long id);
}

