package com.tttn.warehouseqr.modules.masterdata.customer.controller;

import com.tttn.warehouseqr.modules.masterdata.customer.entity.Customer;
import com.tttn.warehouseqr.modules.masterdata.customer.repository.CustomerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/customers") // Đường dẫn API mà Web sẽ gọi
public class CustomerApiController {

    private final CustomerRepository customerRepo;

    // Tiêm Repository vào Controller
    public CustomerApiController(CustomerRepository customerRepo) {
        this.customerRepo = customerRepo;
    }

    @GetMapping
    public ResponseEntity<?> getAllCustomers() {
        try {
            // Lấy toàn bộ khách hàng từ bảng 'customers'
            List<Customer> customers = customerRepo.findAll();

            // Trả về dữ liệu cho Web (Javascript sẽ nhận được mảng này)
            return ResponseEntity.ok(customers);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi tải danh sách khách hàng: " + e.getMessage());
        }
    }
}