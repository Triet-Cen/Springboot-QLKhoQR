package com.tttn.warehouseqr.modules.dashboard.controller;

import com.tttn.warehouseqr.modules.dashboard.dto.ChartDataDto;
import com.tttn.warehouseqr.modules.inventory.dto.InventoryDashboardDto;
import com.tttn.warehouseqr.modules.inventory.dto.InventoryItemDto;
import com.tttn.warehouseqr.modules.inventory.repository.InventoryHistoryRepository;
import com.tttn.warehouseqr.modules.inventory.service.InventoryService; // 1. NHỚ IMPORT DÒNG NÀY
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
    public void exportToExcel(HttpServletResponse response) throws IOException {
        // 1. Lấy dữ liệu
        List<InventoryItemDto> items = inventoryService.getInventoryItems(null, null);
        InventoryDashboardDto stats = inventoryService.getDashboardStats(items);
        List<Object[]> trendResults = historyRepository.getInOutTrendLast7Days();

        Workbook workbook = new XSSFWorkbook();

        // --- TẠO STYLE CHUNG ---
        // Style cho tiêu đề chính (Bold, Background xanh, chữ trắng)
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        // Style cho số tiền (Định dạng #,##0 VNĐ)
        CellStyle currencyStyle = workbook.createCellStyle();
        DataFormat df = workbook.createDataFormat();
        currencyStyle.setDataFormat(df.getFormat("#,##0 \"VNĐ\""));

        // --- SHEET 1: TỔNG QUAN HỆ THỐNG ---
        Sheet summarySheet = workbook.createSheet("1. Tổng Quan");
        String[] summaryHeaders = {"CHỈ SỐ", "GIÁ TRỊ HIỆN TẠI", "GHI CHÚ"};
        Row sHeader = summarySheet.createRow(0);
        for(int i=0; i<summaryHeaders.length; i++) {
            Cell cell = sHeader.createCell(i);
            cell.setCellValue(summaryHeaders[i]);
            cell.setCellStyle(headerStyle);
        }

        Object[][] summaryData = {
                {"Tổng số mặt hàng", stats.getTotalProducts(), "Các mã SKU khác nhau"},
                {"Tổng số lượng tồn kho", stats.getTotalQuantity(), "Tổng đơn vị sản phẩm"},
                {"Số mặt hàng sắp hết", stats.getLowStockWarnings(), "Cần nhập hàng ngay!"},
                {"Tổng giá trị kho", stats.getTotalInventoryValue(), "Giá trị ước tính hiện tại"},
                {"", "", ""},

        };

        for (int i = 0; i < summaryData.length; i++) {
            Row row = summarySheet.createRow(i + 1);
            row.createCell(0).setCellValue(summaryData[i][0].toString());
            Cell valCell = row.createCell(1);
            if (summaryData[i][1] instanceof Number) {
                valCell.setCellValue(Double.parseDouble(summaryData[i][1].toString()));
                if(i == 3) valCell.setCellStyle(currencyStyle); // Áp dụng định dạng tiền cho hàng Giá trị kho
            } else {
                valCell.setCellValue(summaryData[i][1].toString());
            }
            row.createCell(2).setCellValue(summaryData[i][2].toString());
        }
        summarySheet.autoSizeColumn(0);
        summarySheet.autoSizeColumn(1);
        summarySheet.autoSizeColumn(2);

        // --- SHEET 2: XU HƯỚNG NHẬP XUẤT ---
        Sheet trendSheet = workbook.createSheet("2. Xu Hướng Nhập Xuất");
        Row trendHeader = trendSheet.createRow(0);
        String[] tHeaders = {"Ngày", "Số lượng Nhập (Inbound)", "Số lượng Xuất (Outbound)"};
        for(int i=0; i<tHeaders.length; i++) {
            Cell cell = trendHeader.createCell(i);
            cell.setCellValue(tHeaders[i]);
            cell.setCellStyle(headerStyle);
        }

        int trendRowIdx = 1;
        for (Object[] res : trendResults) {
            Row row = trendSheet.createRow(trendRowIdx++);
            row.createCell(0).setCellValue(res[0].toString());

            Cell inCell = row.createCell(1);
            inCell.setCellValue(Double.parseDouble(res[1].toString()));

            Cell outCell = row.createCell(2);
            outCell.setCellValue(Double.parseDouble(res[2].toString()));
        }
        trendSheet.autoSizeColumn(0);
        trendSheet.autoSizeColumn(1);
        trendSheet.autoSizeColumn(2);
        trendSheet.createFreezePane(0, 1); // Cố định dòng đầu tiên

        // 2. Xuất File
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=BaoCao_WarehouseQR_Professional.xlsx");

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}