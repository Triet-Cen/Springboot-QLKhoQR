package com.tttn.warehouseqr.modules.auth.controller;

import com.tttn.warehouseqr.common.exception.AppException;
import com.tttn.warehouseqr.modules.auth.dto.RegisterRequest;
import com.tttn.warehouseqr.modules.auth.dto.UserCreateRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String index() {
        return "index";
    }

}