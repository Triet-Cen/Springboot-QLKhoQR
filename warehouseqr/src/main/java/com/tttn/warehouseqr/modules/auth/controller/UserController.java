package com.tttn.warehouseqr.modules.auth.controller;

import com.tttn.warehouseqr.common.exception.AppException;
import com.tttn.warehouseqr.modules.auth.dto.UserCreateRequest;
import com.tttn.warehouseqr.modules.auth.dto.UserUpdateRequest;
import com.tttn.warehouseqr.modules.auth.repository.RoleRepository;
import com.tttn.warehouseqr.modules.auth.service.RoleService;
import com.tttn.warehouseqr.modules.auth.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
public class UserController {
    private final UserService userService;
    private final RoleService roleService;

    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    // GET: Hiển thị danh sách
    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "auth/user-list";
    }

    // GET: Mở form thêm mới
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("userRequest", new UserCreateRequest());
        model.addAttribute("roles", roleService.getAllRoles());
        return "auth/user-form";
    }

    // POST: Xử lý lưu người dùng mới
    @PostMapping("/add")
    public String create(@Valid @ModelAttribute("userRequest") UserCreateRequest req,
                         BindingResult result, RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "auth/user-form"; // Nếu lỗi Validation, ở lại form để báo lỗi
        }
        userService.createUser(req);
        ra.addFlashAttribute("successMessage", "Thêm mới thành công!");
        return "redirect:/admin/users";
    }

    // GET: Mở form chỉnh sửa
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("userRequest", userService.getUpdateById(id));
        model.addAttribute("roles", roleService.getAllRoles());
        return "auth/user-form";
    }

    // POST: Xử lý cập nhật
    @PostMapping("/edit")
    public String update(@Valid @ModelAttribute("userRequest") UserUpdateRequest req,
                         BindingResult result, RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "auth/user-form";
        }
        userService.updateUser(req);
        ra.addFlashAttribute("successMessage", "Cập nhật thành công!");
        return "redirect:/admin/users";
    }
}