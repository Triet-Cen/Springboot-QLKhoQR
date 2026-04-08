package com.tttn.warehouseqr.modules.inbound.controller;

import com.tttn.warehouseqr.modules.inbound.dto.InboundRequestDTO;
import com.tttn.warehouseqr.modules.inbound.service.InboundService;
import com.tttn.warehouseqr.modules.masterdata.product.dto.ProductScanDTO;
import com.tttn.warehouseqr.modules.masterdata.product.service.impl.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/inbound")
public class InboundController {

    private final InboundService inboundService;
    private final ProductService productService;

    public InboundController(InboundService inboundService, ProductService productService) {
        this.inboundService = inboundService;
        this.productService = productService;
    }


    @GetMapping("/scan-item")
    @ResponseBody
    public ResponseEntity<?> scanItem(@RequestParam String sku, @RequestParam String lotCode) {
        try {
            // Gọi hàm xử lý logic từ ProductService mà bạn vừa viết
            ProductScanDTO data = productService.getProductForScan(sku, lotCode);
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
            // Gọi service xử lý nghiệp vụ trừ PO, cộng tồn kho mà ta đã viết
            inboundService.createInboundReceipt(dto);
            return ResponseEntity.ok("Nhập kho thành công!");
        } catch (Exception e) {
            // Trả về lỗi 400 để JavaScript nhảy vào khối .catch()
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

}
