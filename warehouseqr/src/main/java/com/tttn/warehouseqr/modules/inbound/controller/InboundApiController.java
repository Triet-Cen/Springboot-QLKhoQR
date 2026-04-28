package com.tttn.warehouseqr.modules.inbound.controller;

import com.tttn.warehouseqr.modules.inbound.service.InboundService;
import com.tttn.warehouseqr.modules.masterdata.product.dto.ProductScanDTO;
import com.tttn.warehouseqr.modules.masterdata.product.service.impl.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/inbound")
public class InboundApiController {

    private final ProductService productService;
    private final InboundService inboundService;

    public InboundApiController(ProductService productService, InboundService inboundService) {
        this.productService = productService;
        this.inboundService = inboundService;
    }

    @GetMapping("/scan-item")
    public ResponseEntity<?> scanItem(
            @RequestParam String sku,
            @RequestParam String lotCode,
            @RequestParam Long warehouseId
    ) {
        try {
            ProductScanDTO dto = productService.getProductForScan(sku.trim(), lotCode.trim(), warehouseId);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/parse-csv")
    public ResponseEntity<?> parseCsv(@RequestParam("file") MultipartFile file) {
        try {
            List<ProductScanDTO> items = inboundService.parseCsvToDTO(file);
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

