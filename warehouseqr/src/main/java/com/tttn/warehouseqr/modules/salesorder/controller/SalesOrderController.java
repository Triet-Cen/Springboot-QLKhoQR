package com.tttn.warehouseqr.modules.salesorder.controller;

import com.tttn.warehouseqr.modules.masterdata.customer.repository.CustomerRepository;
import com.tttn.warehouseqr.modules.salesorder.repository.SalesOrderRepository;
import com.tttn.warehouseqr.modules.salesorder.service.SalesOrderImportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.data.domain.Sort;

@Controller
@RequestMapping("/manager/sales-orders")
public class SalesOrderController {

    private final CustomerRepository customerRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final SalesOrderImportService salesOrderImportService;

    public SalesOrderController(CustomerRepository customerRepository,
                                SalesOrderRepository salesOrderRepository,
                                SalesOrderImportService salesOrderImportService) {
        this.customerRepository = customerRepository;
        this.salesOrderRepository = salesOrderRepository;
        this.salesOrderImportService = salesOrderImportService;
    }

    @GetMapping
    public String redirectToCreate() {
        return "redirect:/manager/sales-orders/list";
    }

    @GetMapping("/list")
    public String showListPage(Model model) {
        model.addAttribute("salesOrders", salesOrderRepository.findAll(Sort.by(Sort.Direction.DESC, "id")));
        return "salesorder/sales-order-list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("customers", customerRepository.findAll());
        return "salesorder/sales-order-form";
    }

    @PostMapping("/create")
    public String createSalesOrder(@RequestParam Long customerId,
                                   @RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {
        try {
            salesOrderImportService.createSalesOrderFromCsv(customerId, file);
            redirectAttributes.addFlashAttribute("success", "Tạo đơn bán hàng thành công!");
            return "redirect:/manager/sales-orders/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/manager/sales-orders/create";
        }
    }
}

