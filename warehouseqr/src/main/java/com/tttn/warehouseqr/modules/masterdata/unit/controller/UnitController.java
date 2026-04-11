package com.tttn.warehouseqr.modules.masterdata.unit.controller;

import com.tttn.warehouseqr.modules.masterdata.unit.dto.UnitCreateRequest;
import com.tttn.warehouseqr.modules.masterdata.unit.dto.UnitUpdateRequest;
import com.tttn.warehouseqr.modules.masterdata.unit.entity.Unit;
import com.tttn.warehouseqr.modules.masterdata.unit.service.UnitService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/units")
public class UnitController {
    private final UnitService unitService;

    public UnitController(UnitService unitService) {
        this.unitService = unitService;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "") String keyword, Model model) {
        List<Unit> units = unitService.searchUnits(keyword);
        model.addAttribute("units", units);
        model.addAttribute("keyword", keyword);
        return "masterdata/unit-list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("unitRequest", new UnitCreateRequest());
        return "masterdata/unit-form";
    }

    @PostMapping("/add")
    public String create(@Valid @ModelAttribute("unitRequest") UnitCreateRequest req,
                         BindingResult result,
                         RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "masterdata/unit-form";
        }

        try {
            unitService.createUnit(req);
            ra.addFlashAttribute("successMessage", "Thêm đơn vị tính thành công!");
            return "redirect:/units";
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/units/add";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            model.addAttribute("unitRequest", unitService.getUpdateById(id));
            model.addAttribute("isEdit", true);
            return "masterdata/unit-form";
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/units";
        }
    }

    @PostMapping("/edit")
    public String update(@Valid @ModelAttribute("unitRequest") UnitUpdateRequest req,
                         BindingResult result,
                         Model model,
                         RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", true);
            return "masterdata/unit-form";
        }

        try {
            unitService.updateUnit(req);
            ra.addFlashAttribute("successMessage", "Cập nhật đơn vị tính thành công!");
            return "redirect:/units";
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/units/edit/" + req.getUnitId();
        }
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            unitService.deleteUnit(id);
            ra.addFlashAttribute("successMessage", "Xóa đơn vị tính thành công!");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/units";
    }
}

