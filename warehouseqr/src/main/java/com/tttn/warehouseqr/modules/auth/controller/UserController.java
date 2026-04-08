package com.tttn.warehouseqr.modules.auth.controller;

import com.tttn.warehouseqr.modules.auth.dto.UserCreateRequest;
import com.tttn.warehouseqr.modules.auth.dto.UserUpdateRequest;
import com.tttn.warehouseqr.modules.auth.service.RoleService;
import com.tttn.warehouseqr.modules.auth.service.UserService;
import jakarta.validation.Valid;
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

    // --- MỞ FORM THÊM MỚI ---
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("userRequest", new UserCreateRequest());
        model.addAttribute("roles", roleService.getAllRoles());
        return "auth/user-add";
    }

    // --- XỬ LÝ LƯU MỚI ---
    @PostMapping("/add")
    public String create(@Valid @ModelAttribute("userRequest") UserCreateRequest req,
                         BindingResult result, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("roles", roleService.getAllRoles());
            return "auth/user-add"; // Trả về trang add nếu có lỗi
        }
        userService.createUser(req);
        ra.addFlashAttribute("successMessage", "Thêm mới thành công!");
        return "redirect:/admin/users";
    }
    // --- MỞ FORM CẬP NHẬT ---
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        UserUpdateRequest updateReq = userService.getUpdateById(id);
        model.addAttribute("userRequest", updateReq);
        model.addAttribute("roles", roleService.getAllRoles());
        return "auth/user-edit";
    }

    // --- XỬ LÝ CẬP NHẬT ---
    @PostMapping("/edit")
    public String update(@Valid @ModelAttribute("userRequest") UserUpdateRequest req,
                         BindingResult result, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("roles", roleService.getAllRoles());
            return "auth/user-edit"; // Trả về trang edit nếu có lỗi
        }
        userService.updateUser(req);
        ra.addFlashAttribute("successMessage", "Cập nhật thành công!");
        return "redirect:/admin/users";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        userService.deleteUser(id); // Bạn cần viết hàm này trong Service
        ra.addFlashAttribute("successMessage", "Xóa người dùng thành công!");
        return "redirect:/admin/users";
    }
}