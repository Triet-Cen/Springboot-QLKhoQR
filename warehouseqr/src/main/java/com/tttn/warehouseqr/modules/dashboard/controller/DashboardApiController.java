package com.tttn.warehouseqr.modules.dashboard.controller;

import com.tttn.warehouseqr.modules.dashboard.dto.ChartDataDto;
import com.tttn.warehouseqr.modules.inventory.repository.InventoryHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardApiController {

    private final InventoryHistoryRepository historyRepository;

    @GetMapping("/trends")
    public ResponseEntity<ChartDataDto> getInventoryTrends() {
        List<Object[]> results = historyRepository.getInOutTrendLast7Days();

        List<String> labels = new ArrayList<>();
        List<Double> inboundData = new ArrayList<>();
        List<Double> outboundData = new ArrayList<>();

        // Map dữ liệu từ SQL Object sang DTO
        for (Object[] row : results) {
            labels.add(row[0].toString()); // date_label
            inboundData.add(Double.parseDouble(row[1].toString())); // inbound_qty
            outboundData.add(Double.parseDouble(row[2].toString())); // outbound_qty
        }

        ChartDataDto chartData = new ChartDataDto(labels, inboundData, outboundData);
        return ResponseEntity.ok(chartData);
    }
}