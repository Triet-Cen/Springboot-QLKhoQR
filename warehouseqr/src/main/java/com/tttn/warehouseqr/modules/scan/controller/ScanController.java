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
    @GetMapping
    public String showScanStation(Model model) {
        // 1. Lấy dữ liệu thực tế từ Database
        List<Supplier> suppliers = supplierService.getAllSuppliers();

        // 2. Gán vào Model để Thymeleaf có thể đọc được
        model.addAttribute("suppliers", suppliers);
        List<Warehouse> warehouses = warehouseServicesImp.getAllWarehouse();
        model.addAttribute("warehouses", warehouses);

        // 3. Trả về đúng tên file giao diện
        return "inboundOutboundTransfer/inboundOutboundTransfer";
    }

    // 2. Hàm AJAX: Nhận dữ liệu ngầm từ Javascript Fetch API để không bị load lại trang làm tắt Camera
    @PostMapping("/outbound")
    @ResponseBody
    public ResponseEntity<?> submitOutbound(@RequestBody ScanSubmitDTO request) {
        try {
            // Tạm fix cứng userId = 1
            outboundService.processOutboundList(request, 1L);
            return ResponseEntity.ok("Xác nhận xuất kho thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/transfer")
    @ResponseBody // Quan trọng: để trả về JSON cho Fetch API thay vì trả về trang web
    public ResponseEntity<?> submitTransfer(@RequestBody TransferRequestDTO request) {
        try {
            // Tạm fix userId = 1, request chứa thông tin fromWarehouseId, toWarehouseId và items
            transferOrderServices.processTransfer(request, 1L);
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

}
