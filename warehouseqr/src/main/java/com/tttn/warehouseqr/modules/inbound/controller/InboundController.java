package com.tttn.warehouseqr.modules.inbound.controller;

import com.tttn.warehouseqr.modules.inbound.dto.InboundRequestDTO;
import com.tttn.warehouseqr.modules.inbound.service.InboundService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/inbound")
public class InboundController {

    private final InboundService inboundService;


    public InboundController(InboundService inboundService) {
        this.inboundService = inboundService;
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
