package com.tttn.warehouseqr.modules.auth.controller;

import com.tttn.warehouseqr.common.exception.AppException;
import com.tttn.warehouseqr.modules.auth.dto.RegisterRequest;
import com.tttn.warehouseqr.modules.auth.dto.UserCreateRequest;
import com.tttn.warehouseqr.modules.auth.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {
    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/auth/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/auth/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register"; // Trả về file register.html
    }

    @PostMapping("/auth/register")
    public String registerUser(@Valid @ModelAttribute("registerRequest") RegisterRequest req,
                               BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "auth/register";
        }
        try {
            userService.register(req);
            return "redirect:/auth/login?registerSuccess";
        } catch (AppException e) {
            model.addAttribute("errorMessage", e.getErrorCode().getMessage());
            return "auth/register";
        }
    }
}