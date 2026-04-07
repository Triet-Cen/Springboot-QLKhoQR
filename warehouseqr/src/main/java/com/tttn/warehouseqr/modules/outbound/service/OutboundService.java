package com.tttn.warehouseqr.modules.outbound.service;

import com.tttn.warehouseqr.modules.scan.dto.ScanSubmitDTO;

public interface OutboundService {
    public void processOutboundList(ScanSubmitDTO request, Long userId);
}
