package com.tttn.warehouseqr.modules.scan.controller;

import com.tttn.warehouseqr.modules.outbound.service.OutboundService;
import com.tttn.warehouseqr.modules.scan.dto.ScanSubmitDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/scan-station")
public class ScanController {
    private final OutboundService outboundService;

    public ScanController(OutboundService outboundService) {
        this.outboundService = outboundService;
    }
    @GetMapping
    public String openScanStation() {
        return "inboundOutboundTransfer/inboundOutboundTransfer";
    }

    // 2. Hàm AJAX: Nhận dữ liệu ngầm từ Javascript Fetch API để không bị load lại trang làm tắt Camera
    @PostMapping("/outbound")
    @ResponseBody
    public ResponseEntity<?> submitOutbound(@RequestBody ScanSubmitDTO request) {
        try {
            // Tạm fix cứng userId = 1
            outboundService.processOutboundList(request, 1L);
            return ResponseEntity.ok("Xác nhận xuất kho thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
