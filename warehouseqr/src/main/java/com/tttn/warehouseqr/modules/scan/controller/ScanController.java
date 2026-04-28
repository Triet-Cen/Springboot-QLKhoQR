package com.tttn.warehouseqr.modules.scan.controller;

import com.tttn.warehouseqr.modules.masterdata.supplier.entity.Supplier;
import com.tttn.warehouseqr.modules.masterdata.supplier.service.implement.SupplierServiceImpl;
import com.tttn.warehouseqr.modules.masterdata.warehouse.dto.WarehouseLocationDTO;
import com.tttn.warehouseqr.modules.masterdata.warehouse.entity.Warehouse;
import com.tttn.warehouseqr.modules.masterdata.warehouse.services.imp.WarehouseServiceImpl;
import com.tttn.warehouseqr.modules.outbound.service.OutboundService;
import com.tttn.warehouseqr.modules.scan.dto.ScanSubmitDTO;
import com.tttn.warehouseqr.modules.transfer.dto.TransferRequestDTO;
import com.tttn.warehouseqr.modules.transfer.services.TransferOrderServices;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/scan-station")
public class ScanController {

    private final OutboundService outboundService;

    private final SupplierServiceImpl supplierService;

    private final WarehouseServiceImpl warehouseServicesImp;

    private final TransferOrderServices transferOrderServices;

    public ScanController(OutboundService outboundService, SupplierServiceImpl supplierService, WarehouseServiceImpl warehouseServicesImp, TransferOrderServices transferOrderServices) {
        this.outboundService = outboundService;
        this.supplierService = supplierService;
        this.warehouseServicesImp = warehouseServicesImp;
        this.transferOrderServices = transferOrderServices;
    }

    private void loadScanStationData(Model model) {
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        model.addAttribute("suppliers", suppliers);
        List<Warehouse> warehouses = warehouseServicesImp.getAllWarehouse();
        model.addAttribute("warehouses", warehouses);
    }

    //FIX: Tự động Redirect về trang Inbound cho chuẩn URL
    @GetMapping
    public String showScanStation() {
        return "redirect:/scan-station/inbound";
    }

    @GetMapping("/inbound")
    public String showInboundStation(Model model) {
        loadScanStationData(model);
        return "inboundOutboundTransfer/scan-inbound";
    }

    @GetMapping("/outbound")
    public String showOutboundStation(Model model) {
        loadScanStationData(model);
        return "inboundOutboundTransfer/scan-outbound";
    }


    @GetMapping("/transfer")
    public String showTransferStation(Model model) {
        loadScanStationData(model);
        return "inboundOutboundTransfer/scan-transfer";
    }


    // 2. Hàm AJAX: Nhận dữ liệu ngầm từ Javascript Fetch API để không bị load lại trang làm tắt Camera
    @PostMapping("/outbound")
    @ResponseBody
    public ResponseEntity<?> submitOutbound(@RequestBody ScanSubmitDTO request) {
        try {
            Long userId = com.tttn.warehouseqr.common.util.SecurityUtils.getCurrentUserId();
            outboundService.processOutboundList(request, userId);
            return ResponseEntity.ok("Xác nhận xuất kho thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/transfer")
    @ResponseBody // Quan trọng: để trả về JSON cho Fetch API thay vì trả về trang web
    public ResponseEntity<?> submitTransfer(@RequestBody TransferRequestDTO request) {
        try {
            Long userId = com.tttn.warehouseqr.common.util.SecurityUtils.getCurrentUserId();
            transferOrderServices.processTransfer(request, userId);
            return ResponseEntity.ok("Xác nhận điều chuyển hàng hóa thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    @GetMapping("/warehouses/{warehouseId}/locations")
    @ResponseBody
    public ResponseEntity<List<WarehouseLocationDTO>> getLocationsByWarehouse(@PathVariable Long warehouseId) {
        try {
            List<WarehouseLocationDTO> locations = warehouseServicesImp.getLocationsByWarehouseId(warehouseId);
            return ResponseEntity.ok(locations);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }


    // Quét QR kiểm kê
    @GetMapping("/stocktake")
    public String showStocktakeScan(@RequestParam("sessionId") Long sessionId,
                                    @RequestParam(value = "returnUrl", required = false) String returnUrl,
                                    Model model) {
        loadScanStationData(model);
        model.addAttribute("sessionId", sessionId);
        model.addAttribute("returnUrl", returnUrl != null ? returnUrl : "/admin/stocktake");
        model.addAttribute("mode", "stocktake");
        return "inboundOutboundTransfer/scan-inbound";
    }

}
