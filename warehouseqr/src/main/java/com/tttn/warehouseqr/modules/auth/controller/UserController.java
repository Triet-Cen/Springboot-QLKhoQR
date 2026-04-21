package com.tttn.warehouseqr.modules.auth.controller;

import com.tttn.warehouseqr.common.exception.AppException;
import com.tttn.warehouseqr.modules.auth.dto.UserCreateRequest;
import com.tttn.warehouseqr.modules.auth.dto.UserUpdateRequest;
import com.tttn.warehouseqr.modules.auth.service.RoleService;
import com.tttn.warehouseqr.modules.auth.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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

    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "auth/user-list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("userRequest", new UserCreateRequest());
        model.addAttribute("roles", roleService.getAllRoles());
        return "auth/user-add";
    }

    @PostMapping("/add")
    public String create(@Valid @ModelAttribute("userRequest") UserCreateRequest req,
                         BindingResult result,
                         @RequestParam("imageFile") MultipartFile imageFile,
                         Model model,
                         RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("roles", roleService.getAllRoles());
            return "auth/user-add";
        }

        try {
            userService.createUserWithAvatar(req, imageFile);
            ra.addFlashAttribute("successMessage", "Thêm mới người dùng thành công!");
            return "redirect:/admin/users";
        } catch (AppException e) {
            // Bắt lỗi trùng user từ Service ném ra
            model.addAttribute("errorMessage", e.getErrorCode().getMessage());
            model.addAttribute("roles", roleService.getAllRoles());
            return "auth/user-add"; // Quay lại trang add và mang theo errorMessage
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi hệ thống: " + e.getMessage());
            model.addAttribute("roles", roleService.getAllRoles());
            return "auth/user-add";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("userRequest", userService.getUpdateById(id));
        model.addAttribute("roles", roleService.getAllRoles());
        return "auth/user-edit";
    }

    @PostMapping("/edit")
    public String update(@Valid @ModelAttribute("userRequest") UserUpdateRequest req,
                         BindingResult result,
                         @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                         Model model,
                         RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("roles", roleService.getAllRoles());
            return "auth/user-edit";
        }

        try {
            userService.updateUserWithAvatar(req, imageFile);
            ra.addFlashAttribute("successMessage", "Cập nhật thành công!");
            return "redirect:/admin/users";
        } catch (AppException e) {
            model.addAttribute("errorMessage", e.getErrorCode().getMessage());
            model.addAttribute("roles", roleService.getAllRoles());
            return "auth/user-edit";
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            userService.deleteUser(id);
            ra.addFlashAttribute("successMessage", "Đã xóa người dùng thành công!");
        } catch (AppException e) {
            ra.addFlashAttribute("errorMessage", e.getErrorCode().getMessage());
        }
        return "redirect:/admin/users"; // Đã sửa từ /users thành /admin/users
    }
}