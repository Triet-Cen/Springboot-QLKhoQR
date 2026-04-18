package com.tttn.warehouseqr.modules.masterdata.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductScanDTO {
    // 1. ID để lưu vào bảng InboundReceiptItem (Backend cần)
    private Long productId;

    // 2. Tên để hiển thị lên danh sách chờ xử lý (Người dùng cần thấy)
    private String productName;

    // 3. ID của lô hàng để cộng tồn kho chính xác vào bảng balances
    private Long batchId;

    // 4. Mã lô hàng để hiển thị trên danh sách và Modal phiếu nhập
    private String lotCode;

    // 5. SKU để đối soát nếu cần
    private String sku;

    //6. Số lượng mong đợi
    private Double expectedQty;

    //7. Số lượng thực nhận
    private Double actualQty;

    // 8. ID vị trí Kho
    private Long locationId;

    // 9. Mã vị trí (ví dụ: KE-A-01)
    private String locationCode;

    //10. Giá nhập
    private Double importPrice;

    //11. Id Nhà Cung Cấp
    private Long supplierId;
    //12. Id Mã kho
    private Long warehouseId;

    public ProductScanDTO(Long productId, String productName, Long batchId, String lotCode, String sku, Double expectedQty, Double actualQty, Long locationId, String locationCode, Double importPrice) {
        this.productId = productId;
        this.productName = productName;
        this.batchId = batchId;
        this.lotCode = lotCode;
        this.sku = sku;
        this.expectedQty = expectedQty;
        this.actualQty = actualQty;
        this.locationId = locationId;
        this.locationCode = locationCode;
        this.importPrice = importPrice;
    }

    public ProductScanDTO(Long productId, String productName, Long batchId, String lotCode, String sku, double v, Long locationId, String locationCode, double v1) {
        this.productId = productId;
        this.productName = productName;
        this.batchId = batchId;
        this.lotCode = lotCode;
        this.sku = sku;
        this.locationId = locationId;
        this.locationCode = locationCode;

    }
}
