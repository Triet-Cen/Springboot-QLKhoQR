package com.tttn.warehouseqr.modules.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class InventoryDetailDto {
    private String locationCode; // Mã vị trí (Kệ/Ô)
    private String lotCode;      // Mã lô hàng
    private BigDecimal qty;      // Số lượng tại vị trí đó
}