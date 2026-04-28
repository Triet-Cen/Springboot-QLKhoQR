package com.tttn.warehouseqr.modules.Location.controller;

import com.tttn.warehouseqr.modules.Location.dto.WarehouseZoneFormDTO;
import com.tttn.warehouseqr.modules.Location.repository.LocationWarehouseRepository;
import com.tttn.warehouseqr.modules.Location.repository.LocationWarehouseZoneManageRepository;
import com.tttn.warehouseqr.modules.masterdata.warehouse.entity.Warehouse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/warehouses/manage")
public class LocationWarehousePageController {

    private final LocationWarehouseRepository locationWarehouseRepository;
    private final LocationWarehouseZoneManageRepository locationWarehouseZoneManageRepository;

    public LocationWarehousePageController(LocationWarehouseRepository locationWarehouseRepository,
                                           LocationWarehouseZoneManageRepository locationWarehouseZoneManageRepository) {
        this.locationWarehouseRepository = locationWarehouseRepository;
        this.locationWarehouseZoneManageRepository = locationWarehouseZoneManageRepository;
    }

    @GetMapping("/create")
    public String createWarehouseForm(Model model) {
        model.addAttribute("warehouse", new Warehouse());
        return "Location/Warehouse-manage/warehouse-form";
    }

    @PostMapping("/save")
    public String saveWarehouse(@ModelAttribute("warehouse") Warehouse warehouse,
                                RedirectAttributes redirectAttributes) {
        try {
            String code = warehouse.getWarehouseCode() != null ? warehouse.getWarehouseCode().trim() : "";
            String name = warehouse.getWarehouseName() != null ? warehouse.getWarehouseName().trim() : "";
            String address = warehouse.getWarehouseAddress() != null ? warehouse.getWarehouseAddress().trim() : "";

            if (code.isBlank()) {
                throw new RuntimeException("Mã kho không được để trống.");
            }
            if (name.isBlank()) {
                throw new RuntimeException("Tên kho không được để trống.");
            }
            if (locationWarehouseRepository.existsByWarehouseCode(code)) {
                throw new RuntimeException("Mã kho đã tồn tại: " + code);
            }

            warehouse.setWarehouseCode(code);
            warehouse.setWarehouseName(name);
            warehouse.setWarehouseAddress(address);

            locationWarehouseRepository.save(warehouse);

            redirectAttributes.addFlashAttribute("successMessage", "Thêm kho thành công.");
            return "redirect:/warehouses/locations";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/warehouses/manage/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String editWarehouseForm(@PathVariable long id, Model model) {
        Warehouse warehouse = locationWarehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kho."));
        model.addAttribute("warehouse", warehouse);
        return "Location/Warehouse-manage/warehouse-form";
    }

    @PostMapping("/update")
    public String updateWarehouse(@ModelAttribute("warehouse") Warehouse warehouse,
                                  RedirectAttributes redirectAttributes) {
        try {
            if (warehouse.getWarehouseId() <= 0) {
                throw new RuntimeException("Thiếu ID kho.");
            }

            Warehouse oldWarehouse = locationWarehouseRepository.findById(warehouse.getWarehouseId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy kho."));

            String code = warehouse.getWarehouseCode() != null ? warehouse.getWarehouseCode().trim() : "";
            String name = warehouse.getWarehouseName() != null ? warehouse.getWarehouseName().trim() : "";
            String address = warehouse.getWarehouseAddress() != null ? warehouse.getWarehouseAddress().trim() : "";

            if (code.isBlank()) {
                throw new RuntimeException("Mã kho không được để trống.");
            }
            if (name.isBlank()) {
                throw new RuntimeException("Tên kho không được để trống.");
            }
            if (locationWarehouseRepository.existsByWarehouseCodeAndWarehouseIdNot(code, warehouse.getWarehouseId())) {
                throw new RuntimeException("Mã kho đã tồn tại: " + code);
            }

            oldWarehouse.setWarehouseCode(code);
            oldWarehouse.setWarehouseName(name);
            oldWarehouse.setWarehouseAddress(address);

            locationWarehouseRepository.save(oldWarehouse);

            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật kho thành công.");
            return "redirect:/warehouses/locations";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/warehouses/manage/edit/" + warehouse.getWarehouseId();
        }
    }

    @GetMapping("/zones/create")
    public String createZoneForm(Model model) {
        model.addAttribute("zone", new WarehouseZoneFormDTO());
        model.addAttribute("warehouses", locationWarehouseRepository.findAll());
        return "Location/Zone-manage/zone-form";
    }

    @PostMapping("/zones/save")
    public String saveZone(@ModelAttribute("zone") WarehouseZoneFormDTO zone,
                           RedirectAttributes redirectAttributes) {
        try {
            String code = zone.getZoneCode() != null ? zone.getZoneCode().trim() : "";
            String name = zone.getZoneName() != null ? zone.getZoneName().trim() : "";

            if (zone.getWarehouseId() == null) {
                throw new RuntimeException("Vui lòng chọn kho.");
            }
            if (code.isBlank()) {
                throw new RuntimeException("Mã khu vực không được để trống.");
            }
            if (name.isBlank()) {
                throw new RuntimeException("Tên khu vực không được để trống.");
            }
            if (locationWarehouseZoneManageRepository.countByZoneCode(code) > 0) {
                throw new RuntimeException("Mã khu vực đã tồn tại: " + code);
            }

            locationWarehouseZoneManageRepository.insertZone(code, name, zone.getWarehouseId());

            redirectAttributes.addFlashAttribute("successMessage", "Thêm zone thành công.");
            return "redirect:/warehouses/locations";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/warehouses/manage/zones/create";
        }
    }

    @GetMapping("/zones/edit/{id}")
    public String editZoneForm(@PathVariable Long id, Model model) {
        var zone = locationWarehouseZoneManageRepository.findZoneEditViewById(id);
        if (zone == null) {
            throw new RuntimeException("Không tìm thấy zone.");
        }

        WarehouseZoneFormDTO dto = new WarehouseZoneFormDTO();
        dto.setZoneId(zone.getZoneId());
        dto.setZoneCode(zone.getZoneCode());
        dto.setZoneName(zone.getZoneName());
        dto.setWarehouseId(zone.getWarehouseId());

        model.addAttribute("zone", dto);
        model.addAttribute("warehouses", locationWarehouseRepository.findAll());
        return "Location/Zone-manage/zone-form";
    }

    @PostMapping("/zones/update")
    public String updateZone(@ModelAttribute("zone") WarehouseZoneFormDTO zone,
                             RedirectAttributes redirectAttributes) {
        try {
            String code = zone.getZoneCode() != null ? zone.getZoneCode().trim() : "";
            String name = zone.getZoneName() != null ? zone.getZoneName().trim() : "";

            if (zone.getWarehouseId() == null) {
                throw new RuntimeException("Vui lòng chọn kho.");
            }
            if (code.isBlank()) {
                throw new RuntimeException("Mã khu vực không được để trống.");
            }
            if (name.isBlank()) {
                throw new RuntimeException("Tên khu vực không được để trống.");
            }
            if (locationWarehouseZoneManageRepository.countByZoneCodeAndZoneIdNot(code, zone.getZoneId()) > 0) {
                throw new RuntimeException("Mã khu vực đã tồn tại: " + code);
            }

            locationWarehouseZoneManageRepository.updateZone(
                    zone.getZoneId(),
                    code,
                    name,
                    zone.getWarehouseId()
            );

            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật zone thành công.");
            return "redirect:/warehouses/locations";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/warehouses/manage/zones/edit/" + zone.getZoneId();
        }
    }
}