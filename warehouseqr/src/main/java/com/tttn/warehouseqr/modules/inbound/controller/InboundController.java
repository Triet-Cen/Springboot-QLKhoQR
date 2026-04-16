package com.tttn.warehouseqr.modules.inbound.controller;

import com.tttn.warehouseqr.common.util.SecurityUtils;
import com.tttn.warehouseqr.modules.inbound.dto.InboundRequestDTO;
import com.tttn.warehouseqr.modules.inbound.service.InboundService;
import com.tttn.warehouseqr.modules.inbound.service.impl.InboundServiceImpl;
import com.tttn.warehouseqr.modules.masterdata.product.dto.ProductScanDTO;
import com.tttn.warehouseqr.modules.masterdata.product.service.impl.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/api/inbound")
public class InboundController {

    private final InboundServiceImpl inboundService;
    private final ProductService productService;
    private final SecurityUtils  securityUtils;

    public InboundController(InboundServiceImpl inboundService, ProductService productService, SecurityUtils securityUtils) {
        this.inboundService = inboundService;
        this.productService = productService;
        this.securityUtils = securityUtils;
    }


    @GetMapping("/scan-item")
    @ResponseBody
    public ResponseEntity<?> scanItem(@RequestParam String sku, @RequestParam String lotCode, @RequestParam Long warehouseId) {
        try {
            // Gọi hàm xử lý logic từ ProductService mà bạn vừa viết
            ProductScanDTO data = productService.getProductForScan(sku, lotCode,warehouseId);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            // Trả về thông báo lỗi cụ thể để Frontend hiển thị Alert
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/confirm")
    @ResponseBody // Báo cho Spring trả về dữ liệu/text thay vì tìm file HTML
    public ResponseEntity<String> processInbound(@RequestBody InboundRequestDTO dto) {
        try {
            // Tạm thời lấy ID người dùng là 1 (sau này thay bằng SecurityContext)
            Long userId = securityUtils.getCurrentUserId();
            // Gọi service xử lý nghiệp vụ trừ PO, cộng tồn kho mà ta đã viết
            inboundService.createInboundReceipt(dto, userId);
            return ResponseEntity.ok("Nhập kho thành công!");
        } catch (Exception e) {
            // Trả về lỗi 400 để JavaScript nhảy vào khối .catch()
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    @PostMapping("/parse-csv")
    @ResponseBody
    public ResponseEntity<?> parseInboundCsv(@RequestParam("file") MultipartFile file) {
        try {
            // Gọi hàm parse từ InboundService
            List<ProductScanDTO> data = inboundService.parseCsvToDTO(file);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi file Inbound: " + e.getMessage());
        }
    }

}
