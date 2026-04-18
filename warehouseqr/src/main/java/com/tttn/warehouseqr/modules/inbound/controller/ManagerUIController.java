package com.tttn.warehouseqr.modules.inbound.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manager")
public class ManagerUIController {

    @GetMapping("/inbound/approval")
    public String showInboundApprovalPage(Model model) {
        // ĐÃ SỬA: Thêm chữ 'n' vào chữ inbound
        return "inboundOutboundTransfer/manager-inbound-approval";
    }


}
