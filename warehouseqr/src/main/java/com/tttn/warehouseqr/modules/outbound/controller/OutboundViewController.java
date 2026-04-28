package com.tttn.warehouseqr.modules.outbound.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OutboundViewController {

    // Khi gõ localhost:8080/outbound/history, trình duyệt sẽ mở file HTML này
    @GetMapping("/outbound/history")
    public String outboundHistoryPage() {
        return "outbound-history";
    }
}