package com.tttn.warehouseqr.modules.stocktake.dto;

import lombok.Data;

@Data
public class ScanRequest {
    private Long sessionId;
    private String qrContent;
}