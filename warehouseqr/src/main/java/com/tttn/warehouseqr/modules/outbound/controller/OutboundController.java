package com.tttn.warehouseqr.modules.outbound.controller;

import com.tttn.warehouseqr.modules.outbound.request.OutboundRequestDTO;
import com.tttn.warehouseqr.modules.outbound.service.impl.OutboundServiceImpl;
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
        Long userId = 1L;
        try { return ResponseEntity.ok(outboundServiceImpl.confirmOutbound(request, userId)); }
        catch (Exception e) { return ResponseEntity.badRequest().body(e.getMessage()); }
    }

    // API 3: LẤY LỊCH SỬ (ĐÃ FIX LỖI)
    @GetMapping("/history")
    public ResponseEntity<?> getHistory() {
        List<OutboundReceipt> receipts = receiptRepo.findAll();
        receipts.sort((a,b) -> b.getId().compareTo(a.getId()));

        List<Map<String, Object>> result = new ArrayList<>();
        for (OutboundReceipt r : receipts) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", r.getId());
            map.put("receiptCode", r.getOutboundReceiptCode());
            map.put("createdAt", r.getCreatedAt());
            map.put("status", r.getStatus());

            // Log để debug: Bạn xem ở console IntelliJ xem nó in ra số mấy nhé
            System.out.println("Phiếu " + r.getOutboundReceiptCode() + " có CustomerID: " + r.getCustomerId());

            String customerName = "Khách lẻ";
            if (r.getCustomerId() != null) {
                customerName = customerRepo.findById(r.getCustomerId())
                        .map(c -> c.getCustomerName()).orElse("Khách lẻ");
            }
            map.put("customer", customerName);
            result.add(map);
        }
        return ResponseEntity.ok(result);

    }

    // API 4: CHI TIẾT MÓN HÀNG (GIỮ NGUYÊN)
    @GetMapping("/history/{id}/items")
    public ResponseEntity<?> getHistoryItems(@PathVariable Long id) {
        List<OutboundReceiptItem> items = itemRepo.findByOutboundReceiptId(id);
        List<Map<String, Object>> result = new ArrayList<>();
        for(OutboundReceiptItem item : items) {
            Map<String, Object> map = new HashMap<>();
            map.put("actualQty", item.getActualQty());
            String pName = productRepo.findById(item.getProductId()).map(p -> p.getProductName()).orElse("SP " + item.getProductId());
            String lCode = batchRepo.findById(item.getBatchId()).map(b -> b.getLotCode()).orElse("Mặc định");
            map.put("productName", pName);
            map.put("lotCode", lCode);
            result.add(map);
        }
        return ResponseEntity.ok(result);
    }
}