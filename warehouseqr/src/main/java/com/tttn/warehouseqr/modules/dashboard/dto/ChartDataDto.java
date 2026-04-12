package com.tttn.warehouseqr.modules.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChartDataDto {
    private List<String> labels;      // Danh sách các ngày (VD: ["01/04", "02/04", ...])
    private List<Double> inboundData; // Dữ liệu nhập kho
    private List<Double> outboundData;// Dữ liệu xuất kho
}