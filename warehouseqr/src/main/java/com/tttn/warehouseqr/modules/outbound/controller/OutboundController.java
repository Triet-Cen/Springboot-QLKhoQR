package com.tttn.warehouseqr.modules.outbound.controller;

import com.tttn.warehouseqr.modules.auth.repository.UserRepository;
import com.tttn.warehouseqr.modules.outbound.request.OutboundRequestDTO;
import com.tttn.warehouseqr.modules.outbound.service.impl.OutboundServiceImpl;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tttn.warehouseqr.modules.outbound.entity.OutboundReceipt;
import com.tttn.warehouseqr.modules.outbound.entity.OutboundReceiptItem;
import com.tttn.warehouseqr.modules.outbound.repository.OutboundReceiptRepository;
import com.tttn.warehouseqr.modules.outbound.repository.OutboundReceiptItemRepository;
import com.tttn.warehouseqr.modules.masterdata.product.repository.ProductRepository;
import com.tttn.warehouseqr.modules.masterdata.product.repository.ProductBatchRepository;
import com.tttn.warehouseqr.modules.masterdata.customer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/outbound")
public class OutboundController {
    private final OutboundServiceImpl outboundServiceImpl;

    @Autowired private OutboundReceiptRepository receiptRepo;
    @Autowired private OutboundReceiptItemRepository itemRepo;
    @Autowired private ProductRepository productRepo;
    @Autowired private ProductBatchRepository batchRepo;
    @Autowired private CustomerRepository customerRepo;
    @Autowired private UserRepository userRepo;

    public OutboundController(OutboundServiceImpl outboundServiceImpl) {
        this.outboundServiceImpl = outboundServiceImpl;
    }

    // API 1 & 2 CỦA BẠN (GIỮ NGUYÊN)
    @GetMapping("/suggest/{soCode}")
    public ResponseEntity<?> getSuggestions(@PathVariable String soCode) {
        try { return ResponseEntity.ok(outboundServiceImpl.getPickingSuggestions(soCode)); }
        catch (Exception e) { return ResponseEntity.badRequest().body(e.getMessage()); }
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirm(@RequestBody OutboundRequestDTO request) {
        Long userId = com.tttn.warehouseqr.common.util.SecurityUtils.getCurrentUserId();

        try { return ResponseEntity.ok(outboundServiceImpl.confirmOutbound(request, userId)); }
        catch (Exception e) { return ResponseEntity.badRequest().body(e.getMessage()); }
    }

    // API 3: LẤY LỊCH SỬ (ĐÃ NÂNG CẤP LỌC & PHÂN TRANG)
    @GetMapping("/history")
    public ResponseEntity<?> getHistoryPage(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            java.time.LocalDateTime start = (startDate != null && !startDate.isEmpty()) ? java.time.LocalDate.parse(startDate).atStartOfDay() : null;
            java.time.LocalDateTime end = (endDate != null && !endDate.isEmpty()) ? java.time.LocalDate.parse(endDate).atTime(23, 59, 59) : null;

            // Lấy dữ liệu phân trang, sắp xếp mới nhất lên đầu
            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by("id").descending());
            org.springframework.data.domain.Page<OutboundReceipt> receiptPage = receiptRepo.searchHistory(keyword, start, end, pageable);

            List<Map<String, Object>> content = new ArrayList<>();
            for (OutboundReceipt r : receiptPage.getContent()) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", r.getId());
                map.put("receiptCode", r.getOutboundReceiptCode());
                map.put("createdAt", r.getCreatedAt());
                map.put("status", r.getStatus());

                String customerName = "Khách lẻ";
                if (r.getCustomerId() != null) {
                    customerName = customerRepo.findById(r.getCustomerId()).map(c -> c.getCustomerName()).orElse("Khách lẻ");
                }
                map.put("customer", customerName);

                String creatorName = "Hệ thống";
                if (r.getCreatedBy() != null) {
                    creatorName = userRepo.findById(r.getCreatedBy()).map(u -> u.getFullName()).orElse("Hệ thống");
                }
                map.put("creatorName", creatorName);

                content.add(map);
            }

            // Gói dữ liệu và thông tin trang gửi lên Web
            Map<String, Object> response = new HashMap<>();
            response.put("content", content);
            response.put("currentPage", receiptPage.getNumber());
            response.put("totalItems", receiptPage.getTotalElements());
            response.put("totalPages", receiptPage.getTotalPages());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Lỗi lấy dữ liệu: " + e.getMessage());
        }
    }

    // API 4: CHI TIẾT MÓN HÀNG (ĐÃ FIX LỖI NULL BATCH_ID)
    @GetMapping("/history/{id}/items")
    public ResponseEntity<?> getHistoryItems(@PathVariable Long id) {
        try {
            List<OutboundReceiptItem> items = itemRepo.findByOutboundReceiptId(id);
            List<Map<String, Object>> result = new ArrayList<>();

            for(OutboundReceiptItem item : items) {
                Map<String, Object> map = new HashMap<>();
                map.put("actualQty", item.getActualQty());
                map.put("price", item.getPrice() != null ? item.getPrice() : 0.0);

                String pName = productRepo.findById(item.getProductId())
                        .map(p -> p.getProductName())
                        .orElse("SP " + item.getProductId());

                // 👉 FIX LỖI: Kiểm tra BatchId phải khác null thì mới tìm trong Database
                String lCode = "Mặc định";
                if (item.getBatchId() != null) {
                    lCode = batchRepo.findById(item.getBatchId())
                            .map(b -> b.getLotCode())
                            .orElse("Mặc định");
                }

                map.put("productName", pName);
                map.put("lotCode", lCode);
                map.put("productId", item.getProductId());
                result.add(map);
            }
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Lỗi Backend: " + e.getMessage());
        }
    }
    // ==========================================
    // BƯỚC 2: API 5 - XUẤT FILE EXCEL BÁO CÁO
    // ==========================================
    @GetMapping("/export")
    public void exportToExcel(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            HttpServletResponse response // 👉 Chỉ để thế này thôi nhé
    ) throws java.io.IOException {
        // Cấu hình để trình duyệt tự hiểu đây là file tải về
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=Lich_Su_Xuat_Kho.xlsx");

        // Lấy điều kiện lọc giống hệt lúc xem trên Web
        java.time.LocalDateTime start = (startDate != null && !startDate.isEmpty()) ? java.time.LocalDate.parse(startDate).atStartOfDay() : null;
        java.time.LocalDateTime end = (endDate != null && !endDate.isEmpty()) ? java.time.LocalDate.parse(endDate).atTime(23, 59, 59) : null;

        // Ép lấy 1 triệu dòng (coi như lấy hết dữ liệu thỏa điều kiện để xuất Excel)
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 1000000, org.springframework.data.domain.Sort.by("id").descending());
        List<OutboundReceipt> receipts = receiptRepo.searchHistory(keyword, start, end, pageable).getContent();

        // Tạo file Excel bằng Apache POI
        try (org.apache.poi.ss.usermodel.Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Lịch sử Xuất Kho");

            // Tạo Header
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("STT");
            headerRow.createCell(1).setCellValue("Mã Phiếu Xuất");
            headerRow.createCell(2).setCellValue("Ngày Tạo");
            headerRow.createCell(3).setCellValue("Người Lập");
            headerRow.createCell(4).setCellValue("Khách Hàng");
            headerRow.createCell(5).setCellValue("Trạng Thái");

            // Đổ dữ liệu vào Excel
            int rowNum = 1;
            for (OutboundReceipt r : receipts) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(rowNum - 1);
                row.createCell(1).setCellValue(r.getOutboundReceiptCode());
                row.createCell(2).setCellValue(r.getCreatedAt() != null ? r.getCreatedAt().toString() : "");

                String creatorName = r.getCreatedBy() != null ? userRepo.findById(r.getCreatedBy()).map(u -> u.getFullName()).orElse("Hệ thống") : "Hệ thống";
                row.createCell(3).setCellValue(creatorName);

                String customerName = r.getCustomerId() != null ? customerRepo.findById(r.getCustomerId()).map(c -> c.getCustomerName()).orElse("Khách lẻ") : "Khách lẻ";
                row.createCell(4).setCellValue(customerName);

                row.createCell(5).setCellValue(r.getStatus());
            }

            for (int i = 0; i <= 5; i++) { sheet.autoSizeColumn(i); }
            workbook.write(response.getOutputStream());
        }
    }
}