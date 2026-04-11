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
        try {
            return ResponseEntity.ok(outboundServiceImpl.getPickingSuggestions(soCode));
        } catch (Exception e) {
            // Ép trả về lỗi 400 kèm câu thông báo
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // API 2: Xác nhận xuất kho sau khi đã nhặt hàng xong
    @PostMapping("/confirm")
    public ResponseEntity<?> confirm(@RequestBody OutboundRequestDTO request) {
        Long userId = 1L; // Giả sử user ID

        try {
            // Chạy logic xuất kho (Nếu lỗi sẽ văng xuống block catch bên dưới)
            return ResponseEntity.ok(outboundServiceImpl.confirmOutbound(request, userId));

        } catch (Exception e) {
            // ========================================================
            // CHỐT CHẶN BẢO MẬT: BẮT LỖI VÀ ÉP TRẢ VỀ STATUS 400
            // ========================================================
            // Thay vì để Spring Boot chuyển hướng (302), ta tát thẳng mặt
            // thằng Web bằng mã lỗi 400 cùng câu chửi "TỪ CHỐI XUẤT KHO..."
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}