# 🔧 FIX: Lỗi Không Lấy Được Giá Tiền Khi Xuất Kho

## 📋 Nguyên Nhân Vấn Đề
Khi quét mã Sales Order để lấy gợi ý xuất kho, API `/api/outbound/suggest/` trả về **không có trường giá tiền**, dẫn đến:
- ❌ Giao diện hiển thị giá = **0 đ**
- ❌ Người dùng phải nhập lại thủ công (nhưng không nhân tiện)
- ❌ Phiếu xuất kho không có dữ liệu giá chính xác

---

## ✅ Giải Pháp Thực Hiện

### **1️⃣ Thêm Trường Giá Vào DTO**
📄 File: `OutboundPickingSuggestionDTO.java`

```java
// ❌ CŨ: Không có trường giá
private BigDecimal requiredQty;
private BigDecimal allocatedQty;
private BigDecimal shortageQty;
private List<LocationSuggestion> suggestedLocations;

// ✅ MỚI: Thêm trường giá
private BigDecimal requiredQty;
private BigDecimal allocatedQty;
private BigDecimal shortageQty;
private BigDecimal price;  // 👈 LẤY TỪ SALES ORDER ITEM
private List<LocationSuggestion> suggestedLocations;
```

---

### **2️⃣ Cập Nhật Service Để Lấy Giá Từ Database**
📄 File: `OutboundServiceImpl.java` - Hàm `getPickingSuggestions()`

```java
// ✅ THÊM DÒ: LẤY GIÁ TỪ SALES ORDER ITEM
OutboundPickingSuggestionDTO dto = new OutboundPickingSuggestionDTO();
dto.setSalesOrderId(so.getId());
dto.setApprovalStatus(so.getStatus());
dto.setPaymentStatus(so.getPaymentStatus());
dto.setPaymentMethod(so.getPaymentMethod());
dto.setProductId(item.getProductId());

// 👉 DÒNG MỚI: Copy giá bán từ Sales Order Item
dto.setPrice(item.getUnitPrice());

dto.setRequiredQty(requiredQty);
// ... code tiếp theo
```

---

### **3️⃣ Cải Thiện Frontend Xử Lý Giá**
📄 File: `inboundOutboundTransfer.html`

```javascript
// ✅ CỠI THIỆN HÀM normalizeItemData()
function normalizeItemData(rawData) {
    // 👉 FIX: Convert BigDecimal sang số JavaScript nếu cần
    const rawPrice = rawData.price;
    const detectedPrice = (rawPrice && typeof rawPrice === 'object' 
        ? parseFloat(rawPrice) 
        : rawPrice) 
        || rawData.exportPrice 
        || rawData.unitPrice 
        || rawData.importPrice 
        || 0;
    
    return {
        ...rawData,
        productId: rawData.productId || rawData.product_id || rawData.id,
        // ... các trường khác
        price: detectedPrice,  // ✅ ĐẢM BẢO LÀ SỐ
        exportPrice: detectedPrice,
        importPrice: detectedPrice
    };
}
```

---

## 🔄 Luồng Dữ Liệu Sau Fix

```
1. Frontend quét mã Sales Order (SO-XXXX)
         ↓
2. API /api/outbound/suggest/ được gọi
         ↓
3. Backend tìm SalesOrder + Items
         ↓
4. ✅ MỚI: Lấy unitPrice từ SalesOrderItem
         ↓
5. Trả về OutboundPickingSuggestionDTO với giá
         ↓
6. Frontend nhận giá → normalizeItemData() convert
         ↓
7. ✅ Hiển thị giá đúng trong danh sách
         ↓
8. Khi submit: gửi giá kèm theo items
```

---

## 📊 Kết Quả Kiểm Tra

| Trước Fix | Sau Fix |
|-----------|---------|
| Giá hiển thị: **0 đ** | Giá hiển thị: ✅ **Từ DB** |
| Người dùng phải nhập lại | ✅ Tự động điền giá |
| Phiếu kho thiếu giá | ✅ Đầy đủ thông tin |

---

## 🎯 Tác Động

- ✅ **Tự động lấy giá** từ Sales Order
- ✅ **Giảm nhập liệu thủ công** cho nhân viên kho
- ✅ **Tăng độ chính xác** dữ liệu
- ✅ **Tối ưu hiệu suất** (không cần query thêm)

---

## 📝 Files Sửa Đổi

| File | Thay Đổi |
|------|---------|
| `OutboundPickingSuggestionDTO.java` | ➕ Thêm field `private BigDecimal price;` |
| `OutboundServiceImpl.java` | ➕ Thêm dòng `dto.setPrice(item.getUnitPrice());` |
| `inboundOutboundTransfer.html` | 🔄 Cải thiện xử lý giá trong `normalizeItemData()` |

---

**📅 Hoàn Thành: 2026-04-17**

