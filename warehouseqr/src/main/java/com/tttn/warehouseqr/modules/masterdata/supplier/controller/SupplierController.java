package com.tttn.warehouseqr.modules.masterdata.supplier.controller;

import com.tttn.warehouseqr.modules.masterdata.supplier.dto.SupplierCreateRequest;
import com.tttn.warehouseqr.modules.masterdata.supplier.dto.SupplierUpdateRequest;
import com.tttn.warehouseqr.modules.masterdata.supplier.entity.Supplier;
import com.tttn.warehouseqr.modules.masterdata.supplier.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/suppliers")
public class SupplierController {
    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "") String keyword, Model model){
        List<Supplier> suppliers = supplierService.searchSuppliers(keyword);
        model.addAttribute("suppliers", suppliers);
        model.addAttribute("keyword", keyword);
        return "masterdata/supplier-list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model){
        model.addAttribute("supplierRequest", new SupplierCreateRequest());
        return "masterdata/supplier-form";
    }

    @PostMapping("/add")
    public String create(@Valid @ModelAttribute("supplierRequest") SupplierCreateRequest req,
                         BindingResult result, RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "masterdata/supplier-form"; // Trả về trang form nếu có lỗi validation
        }

        try {
            supplierService.createSupplier(req);
            ra.addFlashAttribute("successMessage", "Thêm mới nhà cung cấp thành công!");
            return "redirect:/suppliers";
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/suppliers/add";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes ra){
        try{
            SupplierUpdateRequest updateReq= supplierService.getUpdateById(id);
            model.addAttribute("supplierRequest", updateReq);
            model.addAttribute("isEdit", true);
            return "masterdata/supplier-form";
        }catch(RuntimeException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/suppliers";
        }
    }

    @PostMapping("/edit")
    public String update(@Valid @ModelAttribute("supplierRequest") SupplierUpdateRequest req,
                         BindingResult result,Model model, RedirectAttributes ra){
        if(result.hasErrors()){
            model.addAttribute("isEdit", true);
            return "masterdata/supplier-form";  // Trả về trang form nếu có lỗi validation
        }
        try {
            supplierService.updateSupplier(req);
            ra.addFlashAttribute("successMessage", "Update supplier successfully!");
            return  "redirect:/suppliers";
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/suppliers/edit/" + req.getSupplierId();
        }
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra){
        try {
            supplierService.deleteSupplier(id);
            ra.addFlashAttribute("successMessage", "Delete supplier successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/suppliers";
    }
}
