package com.tttn.warehouseqr.modules.dashboard.controller;

import com.tttn.warehouseqr.modules.dashboard.dto.ChartDataDto;
import com.tttn.warehouseqr.modules.inventory.dto.InventoryDashboardDto;
import com.tttn.warehouseqr.modules.inventory.dto.InventoryItemDto;
import com.tttn.warehouseqr.modules.inventory.repository.InventoryHistoryRepository;
import com.tttn.warehouseqr.modules.inventory.service.InventoryService; // 1. NHỚ IMPORT DÒNG NÀY
import com.tttn.warehouseqr.modules.masterdata.supplier.entity.Supplier;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardApiController {

    private final InventoryHistoryRepository historyRepository;

    // 2. KHAI BÁO THÊM DÒNG NÀY ĐỂ HẾT LỖI
    private final InventoryService inventoryService;

    @GetMapping("/trends")
    public ResponseEntity<ChartDataDto> getInventoryTrends() {
        // Lấy dữ liệu từ Repository (Ngày, Tổng Nhập, Tổng Xuất)
        List<Object[]> results = historyRepository.getInOutTrendLast7Days();

        List<String> labels = new ArrayList<>();
        List<Double> inboundData = new ArrayList<>();
        List<Double> outboundData = new ArrayList<>();

        for (Object[] res : results) {
            // res[0]: Ngày, res[1]: Nhập, res[2]: Xuất
            labels.add(res[0] != null ? res[0].toString() : "");

            // Dùng Double.valueOf để an toàn hơn parseDouble
            double inbound = (res[1] != null) ? Double.parseDouble(res[1].toString()) : 0.0;
            double outbound = (res[2] != null) ? Double.parseDouble(res[2].toString()) : 0.0;

            inboundData.add(inbound);
            outboundData.add(outbound);
        }

        ChartDataDto chartData = new ChartDataDto();
        chartData.setLabels(labels);
        chartData.setInboundData(inboundData);
        chartData.setOutboundData(outboundData);

        return ResponseEntity.ok(chartData);
    }


    @GetMapping("/export-excel")
    public void exportFullDashboardExcel(HttpServletResponse response) throws IOException {
        // 1. Lấy toàn bộ dữ liệu cần thiết
        List<InventoryItemDto> allItems = inventoryService.getInventoryItems(null, null);
        InventoryDashboardDto stats = inventoryService.getDashboardStats(allItems);

        List<InventoryItemDto> lowStockItems = allItems.stream()
                .filter(item -> item.getTotalQuantity() != null &&
                        item.getTotalQuantity().compareTo(new java.math.BigDecimal("10")) < 0)
                .collect(Collectors.toList());

        List<Object[]> trendResults = historyRepository.getInOutTrendLast7Days();


        // 2. Khởi tạo Workbook
        Workbook workbook = new XSSFWorkbook();
        Sheet summarySheet = workbook.createSheet("1. Tổng Quan & Dự Báo");

        // Style cho Header
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // --- SHEET 1: TỔNG QUAN ---
        String[] headers = {"Chỉ số thống kê", "Giá trị"};
        Row headerRow = summarySheet.createRow(0);
        for(int i=0; i<headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        Object[][] statData = {
                {"Tổng số mặt hàng", stats.getTotalProducts()},
                {"Tổng số lượng tồn kho", stats.getTotalQuantity()},
                {"Số lượng sản phẩm cảnh báo hàng thấp", stats.getLowStockWarnings()},
                {"Tổng giá trị kho hàng (VNĐ)", stats.getTotalInventoryValue()}
        };

        int rowIdx = 1;
        for (Object[] data : statData) {
            Row row = summarySheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(data[0].toString());
            row.createCell(1).setCellValue(data[1].toString());
        }
        summarySheet.autoSizeColumn(0);
        summarySheet.autoSizeColumn(1);

        // --- SHEET 2: XU HƯỚNG NHẬP XUẤT ---
        Sheet trendSheet = workbook.createSheet("2. Biến Động 7 Ngày");
        String[] trendHeaders = {"Ngày", "Nhập kho", "Xuất kho"};
        Row tHeaderRow = trendSheet.createRow(0);
        for(int i=0; i<trendHeaders.length; i++) {
            Cell cell = tHeaderRow.createCell(i);
            cell.setCellValue(trendHeaders[i]);
            cell.setCellStyle(headerStyle);
        }

        int tRowIdx = 1;
        for (Object[] res : trendResults) {
            Row row = trendSheet.createRow(tRowIdx++);
            row.createCell(0).setCellValue(res[0].toString());
            row.createCell(1).setCellValue(Double.parseDouble(res[1].toString()));
            row.createCell(2).setCellValue(Double.parseDouble(res[2].toString()));
        }

        // --- SHEET 3: DANH SÁCH CẢNH BÁO NHẬP HÀNG ---
        Sheet lowStockSheet = workbook.createSheet("3. Cần Nhập Hàng Ngay");
        String[] lowStockHeaders = {"Mã SP", "Tên Sản Phẩm", "Tồn Kho Hiện Tại"};
        Row lsHeaderRow = lowStockSheet.createRow(0);
        for(int i=0; i<lowStockHeaders.length; i++) {
            Cell cell = lsHeaderRow.createCell(i);
            cell.setCellValue(lowStockHeaders[i]);
            cell.setCellStyle(headerStyle);
        }

        int lsRowIdx = 1;
        for (InventoryItemDto item : lowStockItems) {
            Row row = lowStockSheet.createRow(lsRowIdx++);
            row.createCell(0).setCellValue(item.getProductId());
            row.createCell(1).setCellValue(item.getProductName());
            row.createCell(2).setCellValue(item.getTotalQuantity().doubleValue());
        }

        // 3. Cấu hình Response để tải file
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=Bao_Cao_Kho_Tong_Hop.xlsx");

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}