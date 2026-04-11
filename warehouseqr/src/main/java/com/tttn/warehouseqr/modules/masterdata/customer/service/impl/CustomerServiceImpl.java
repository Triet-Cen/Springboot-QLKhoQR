package com.tttn.warehouseqr.modules.masterdata.customer.service.impl;

import com.tttn.warehouseqr.modules.masterdata.customer.dto.CustomerCreateRequest;
import com.tttn.warehouseqr.modules.masterdata.customer.dto.CustomerUpdateRequest;
import com.tttn.warehouseqr.modules.masterdata.customer.entity.Customer;
import com.tttn.warehouseqr.modules.masterdata.customer.repository.CustomerRepository;
import com.tttn.warehouseqr.modules.masterdata.customer.service.CustomerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    private String normalize(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    @Override
    public void createCustomer(CustomerCreateRequest req) {
        String code = normalize(req.getCustomerCode());
        String name = normalize(req.getCustomerName());

        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Customer code is required");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Customer name is required");
        }
        if (customerRepository.existsByCustomerCode(code)) {
            throw new IllegalArgumentException("Customer code already exists");
        }

        Customer customer = new Customer();
        customer.setCustomerCode(code);
        customer.setCustomerName(name);
        customer.setContactPerson(normalize(req.getContactPerson()));
        customer.setPhone(normalize(req.getPhone()));
        customer.setEmail(normalize(req.getEmail()));
        customer.setAddress(normalize(req.getAddress()));

        customerRepository.save(customer);
    }

    @Override
    public void updateCustomer(CustomerUpdateRequest req) {
        Customer customer = customerRepository.findById(req.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer id not found"));

        String newCode = normalize(req.getCustomerCode());
        String newName = normalize(req.getCustomerName());

        if (newCode == null || newCode.isBlank()) {
            throw new RuntimeException("Customer code is required");
        }
        if (newName == null || newName.isBlank()) {
            throw new RuntimeException("Customer name is required");
        }

        boolean codeChanged = !newCode.equalsIgnoreCase(customer.getCustomerCode());
        if (codeChanged && customerRepository.existsByCustomerCode(newCode)) {
            throw new RuntimeException("Customer code already exists");
        }

        customer.setCustomerCode(newCode);
        customer.setCustomerName(newName);
        customer.setContactPerson(normalize(req.getContactPerson()));
        customer.setPhone(normalize(req.getPhone()));
        customer.setEmail(normalize(req.getEmail()));
        customer.setAddress(normalize(req.getAddress()));

        customerRepository.save(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerUpdateRequest getUpdateById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer id not found"));

        CustomerUpdateRequest dto = new CustomerUpdateRequest();
        dto.setCustomerId(customer.getCustomerId());
        dto.setCustomerCode(customer.getCustomerCode());
        dto.setCustomerName(customer.getCustomerName());
        dto.setContactPerson(customer.getContactPerson());
        dto.setPhone(customer.getPhone());
        dto.setEmail(customer.getEmail());
        dto.setAddress(customer.getAddress());

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> searchCustomers(String keyword) {
        String kw = normalize(keyword);
        return customerRepository.searchByKeyword(kw);
    }

    @Override
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer id not found"));
        customerRepository.delete(customer);
    }
}

