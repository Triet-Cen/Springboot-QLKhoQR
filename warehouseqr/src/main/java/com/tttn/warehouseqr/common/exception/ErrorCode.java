package com.tttn.warehouseqr.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // --- System & Generic Errors ---
    UNCATEGORIZED_EXCEPTION("9999", "Lỗi hệ thống chưa xác định", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY("SYS001", "Mã lỗi (Key) không hợp lệ", HttpStatus.BAD_REQUEST),

    // --- Business Logic Errors ---
    USER_NOT_EXISTED("USR001", "Người dùng không tồn tại", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS("USR002", "Tên đăng nhập đã tồn tại", HttpStatus.BAD_REQUEST),
    ROLE_NOT_FOUND("ROL001", "Quyền hạn không hợp lệ", HttpStatus.NOT_FOUND),
    EMAIL_ALREADY_EXISTS("USR005", "Email đã được sử dụng bởi một tài khoản khác", HttpStatus.BAD_REQUEST),
    PHONE_ALREADY_EXISTS("USR006", "Số điện thoại đã được sử dụng bởi một tài khoản khác", HttpStatus.BAD_REQUEST),
    CCCD_ALREADY_EXISTS("USR007", "Số CCCD đã được sử dụng bởi một tài khoản khác", HttpStatus.BAD_REQUEST),


    // --- Validation Errors (Dùng cho DTO) ---
    USERNAME_REQUIRED("VAL001", "Tên đăng nhập không được để trống", HttpStatus.BAD_REQUEST),
    FULLNAME_REQUIRED("VAL002", "Họ tên không được để trống", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID("VAL003", "Email không đúng định dạng", HttpStatus.BAD_REQUEST),
    PASSWORD_TOO_SHORT("VAL004", "Mật khẩu phải từ 6 ký tự trở lên", HttpStatus.BAD_REQUEST),
    ID_REQUIRED("VAL005", "ID không được để trống", HttpStatus.BAD_REQUEST),
    PHONE_REQUIRED("USR003", "Số điện thoại không được để trống", HttpStatus.BAD_REQUEST),
    PHONE_INVALID("USR004", "Số điện thoại không đúng định dạng (10 số)", HttpStatus.BAD_REQUEST),

    // stocktake
    STOCKTAKE_ITEM_NOT_FOUND("STK001", "Không tìm thấy mặt hàng kiểm kê", HttpStatus.NOT_FOUND),
    PRODUCT_NOT_FOUND("PRD001", "Sản phẩm không tồn tại", HttpStatus.NOT_FOUND);

    private final String code;
    private final String message;
    private final HttpStatus statusCode;

    ErrorCode(String code, String message, HttpStatus statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }
}