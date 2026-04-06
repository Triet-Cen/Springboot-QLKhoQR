package com.tttn.warehouseqr.modules.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    @GetMapping("/auth/login")
    public String login() {
        return "auth/login"; // Trả về file login.html
    }

//    @GetMapping("/dashboard")
//    public String dashboard() {
//        return "redirect:/admin/users";
//    }
}