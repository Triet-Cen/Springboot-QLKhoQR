package com.tttn.warehouseqr.common.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 1. Xử lý AppException chủ động từ Service (Ví dụ: Trùng tên đăng nhập)
    @ExceptionHandler(AppException.class)
    public String handleAppException(AppException ex, RedirectAttributes ra) {
        ra.addFlashAttribute("errorMessage", ex.getErrorCode().getMessage());
        return "redirect:/admin/users";
    }

    // 2. Xử lý Validation lỗi từ các Annotation ở DTO (@NotBlank, @Email...)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidation(MethodArgumentNotValidException ex, RedirectAttributes ra) {
        // Lấy tên Enum được ghi trong thuộc tính 'message' của DTO
        String enumKey = ex.getBindingResult().getFieldError().getDefaultMessage();

        ErrorCode errorCode = ErrorCode.INVALID_KEY; // Mặc định nếu không tìm thấy Key

        try {
            // Tra cứu Enum dựa trên chuỗi String nhận được
            errorCode = ErrorCode.valueOf(enumKey);
        } catch (IllegalArgumentException e) {
            // Log lỗi nếu cần: Key trong message không khớp với bất kỳ Enum nào
        }

        ra.addFlashAttribute("errorMessage", errorCode.getMessage());
        return "redirect:/admin/users";
    }

    // 3. Xử lý các lỗi hệ thống không mong muốn khác
    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, RedirectAttributes ra) {
        ra.addFlashAttribute("errorMessage", ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());
        return "redirect:/admin/users";
    }
}