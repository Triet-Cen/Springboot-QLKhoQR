package com.tttn.warehouseqr.modules.masterdata.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

}
