package com.tttn.warehouseqr.modules.outbound.controller;

import com.tttn.warehouseqr.modules.outbound.request.OutboundRequestDTO;
import com.tttn.warehouseqr.modules.outbound.service.impl.OutboundServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/outbound")
public class OutboundController {
    private final OutboundServiceImpl outboundServiceImpl;

    public OutboundController(OutboundServiceImpl outboundServiceImpl) {
        this.outboundServiceImpl = outboundServiceImpl;
    }

    // API 1: Quét mã SO để lấy gợi ý vị trí lấy hàng
    @GetMapping("/suggest/{soCode}")
    public ResponseEntity<?> getSuggestions(@PathVariable String soCode) {
        return ResponseEntity.ok(outboundServiceImpl.getPickingSuggestions(soCode));
    }

    // API 2: Xác nhận xuất kho sau khi đã nhặt hàng xong
    @PostMapping("/confirm")
    public ResponseEntity<?> confirm(@RequestBody OutboundRequestDTO request) {
        // Giả sử lấy userId từ Token/Session, ở đây tôi tạm để 1L
        Long userId = 1L;
        return ResponseEntity.ok(outboundServiceImpl.confirmOutbound(request, userId));
    }
}
