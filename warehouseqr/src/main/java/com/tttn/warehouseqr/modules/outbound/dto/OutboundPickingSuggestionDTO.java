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

    // 👉 3 BIẾN NÀY PHẢI NẰM Ở ĐÂY (TRONG CLASS CHA)
    private Long salesOrderId;
    private String approvalStatus;
    private String paymentStatus;
    // 👉 THÊM DÒNG NÀY ĐỂ TRUYỀN HÌNH THỨC THANH TOÁN (COD / TRANSFER) XUỐNG UI
    private String paymentMethod;

    private Long productId;
    private String productName;
    private String sku;
    private BigDecimal requiredQty; // Số lượng khách đặt trong đơn SO
    private BigDecimal allocatedQty; // Số lượng hệ thống gợi ý lấy được
    private BigDecimal shortageQty; // Số lượng còn thiếu nếu tồn không đủ
    private BigDecimal price; // 👉 Giá bán đơn vị từ Sales Order Item

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
        private BigDecimal suggestedPickQty; // Số lượng cần lấy tại vị trí này
        private Integer fifoRank; // Thứ tự lấy hàng theo FIFO
    }
}