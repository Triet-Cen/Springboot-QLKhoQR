package com.tttn.warehouseqr.modules.Location.controller;

import com.tttn.warehouseqr.modules.Location.entity.StorageLocation;
import com.tttn.warehouseqr.modules.Location.repository.WarehouseZoneRepository;
import com.tttn.warehouseqr.modules.Location.service.StorageLocationService;
import com.tttn.warehouseqr.modules.masterdata.warehouse.services.WarehouseService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/warehouses/locations")
public class StorageLocationPageController {

    private final WarehouseService warehouseService;
    private final StorageLocationService storageLocationService;
    private final WarehouseZoneRepository warehouseZoneRepository;

    public StorageLocationPageController(WarehouseService warehouseService,
                                         StorageLocationService storageLocationService,
                                         WarehouseZoneRepository warehouseZoneRepository) {
        this.warehouseService = warehouseService;
        this.storageLocationService = storageLocationService;
        this.warehouseZoneRepository = warehouseZoneRepository;
    }

    @GetMapping
    public String list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) String status,
            Model model
    ) {
        List<StorageLocation> locations = storageLocationService.search(keyword, warehouseId, status);

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalLocations", locations.size());
        summary.put("usedLocations",
                locations.stream()
                        .filter(l -> l.getUsedCapacity() != null && l.getUsedCapacity() > 0)
                        .count());
        summary.put("fullLocations",
                locations.stream()
                        .filter(l -> "FULL".equalsIgnoreCase(l.getStatus()))
                        .count());
        summary.put("emptyLocations",
                locations.stream()
                        .filter(l -> "EMPTY".equalsIgnoreCase(l.getStatus())
                                || l.getUsedCapacity() == null
                                || l.getUsedCapacity() == 0)
                        .count());

        model.addAttribute("locations", locations);
        model.addAttribute("warehouses", warehouseService.findAll());
        model.addAttribute("keyword", keyword);
        model.addAttribute("warehouseId", warehouseId);
        model.addAttribute("status", status);
        model.addAttribute("summary", summary);

        return "Location/Location-list/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("location", new StorageLocation());
        model.addAttribute("warehouses", warehouseService.findAll());
        model.addAttribute("zones", warehouseZoneRepository.findAll());
        return "Location/Location-form/create-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("location") StorageLocation location) {
        storageLocationService.save(location);
        return "redirect:/warehouses/locations";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        StorageLocation location = storageLocationService.findById(id);

        model.addAttribute("location", location);
        model.addAttribute("warehouses", warehouseService.findAll());
        model.addAttribute("zones", warehouseZoneRepository.findAll());

        return "Location/Location-form/edit-form";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute("location") StorageLocation location) {
        storageLocationService.update(location.getLocationId(), location);
        return "redirect:/warehouses/locations";
    }

    @GetMapping("/trace-inventory-by-location-qr")
    @ResponseBody
    public ResponseEntity<?> traceInventoryByLocationQr(@RequestParam String qrContent) {
        try {
            return ResponseEntity.ok(storageLocationService.traceInventoryByLocationQr(qrContent));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/location-qr/{id}")
    @ResponseBody
    public ResponseEntity<?> getLocationQr(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(storageLocationService.getLocationQrInfo(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}