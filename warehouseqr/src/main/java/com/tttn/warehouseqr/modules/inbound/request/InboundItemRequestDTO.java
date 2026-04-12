package com.tttn.warehouseqr.modules.inbound.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InboundItemRequestDTO {
    private Long productId;
    private Long batchId;      // Lấy từ việc quét mã QR lô hàng
    private Long locationId;   // Vị trí kệ sẽ cất hàng vào
    private Double actualQty;  // Số lượng thực nhập
    private String productName;
    private String lotCode;
    private Double importPrice;
}
