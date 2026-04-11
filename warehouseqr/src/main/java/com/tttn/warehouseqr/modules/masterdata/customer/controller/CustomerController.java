package com.tttn.warehouseqr.modules.masterdata.customer.controller;

import com.tttn.warehouseqr.modules.masterdata.customer.dto.CustomerCreateRequest;
import com.tttn.warehouseqr.modules.masterdata.customer.dto.CustomerUpdateRequest;
import com.tttn.warehouseqr.modules.masterdata.customer.entity.Customer;
import com.tttn.warehouseqr.modules.masterdata.customer.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/customers")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "") String keyword, Model model) {
        List<Customer> customers = customerService.searchCustomers(keyword);
        model.addAttribute("customers", customers);
        model.addAttribute("keyword", keyword);
        return "masterdata/customer-list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("customerRequest", new CustomerCreateRequest());
        return "masterdata/customer-form";
    }

    @PostMapping("/add")
    public String create(@Valid @ModelAttribute("customerRequest") CustomerCreateRequest req,
                         BindingResult result,
                         RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "masterdata/customer-form";
        }

        try {
            customerService.createCustomer(req);
            ra.addFlashAttribute("successMessage", "Thêm khách hàng thành công!");
            return "redirect:/customers";
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/customers/add";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            model.addAttribute("customerRequest", customerService.getUpdateById(id));
            model.addAttribute("isEdit", true);
            return "masterdata/customer-form";
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/customers";
        }
    }

    @PostMapping("/edit")
    public String update(@Valid @ModelAttribute("customerRequest") CustomerUpdateRequest req,
                         BindingResult result,
                         Model model,
                         RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", true);
            return "masterdata/customer-form";
        }

        try {
            customerService.updateCustomer(req);
            ra.addFlashAttribute("successMessage", "Cập nhật khách hàng thành công!");
            return "redirect:/customers";
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/customers/edit/" + req.getCustomerId();
        }
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            customerService.deleteCustomer(id);
            ra.addFlashAttribute("successMessage", "Xóa khách hàng thành công!");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/customers";
    }
}

