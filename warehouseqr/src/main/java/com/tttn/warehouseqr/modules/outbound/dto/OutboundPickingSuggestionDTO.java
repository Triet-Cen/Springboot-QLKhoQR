package com.tttn.warehouseqr.modules.outbound.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OutboundPickingSuggestionDTO {
    private Long productId;
    private String productName;
    private String sku;
    private BigDecimal requiredQty; // Số lượng khách đặt trong đơn SO

    // Danh sách các kệ/lô đang có sẵn sản phẩm này để gợi ý cho nhân viên
    private List<LocationSuggestion> suggestedLocations;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationSuggestion {
        private Long locationId;
        private String locationCode; // Tên kệ (VD: Kệ A1)
        private Long batchId;
        private String lotCode;      // Mã lô hàng
        private BigDecimal availableQty; // Số lượng hiện có tại kệ này
    }
}
