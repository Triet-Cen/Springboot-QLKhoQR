package com.tttn.warehouseqr.modules.auth.controller;

import com.tttn.warehouseqr.common.exception.AppException;
import com.tttn.warehouseqr.modules.auth.dto.RegisterRequest;
import com.tttn.warehouseqr.modules.auth.dto.UserCreateRequest;
import com.tttn.warehouseqr.modules.masterdata.product.entity.Product;
import com.tttn.warehouseqr.modules.masterdata.product.service.impl.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {
    @Autowired
    private ProductService productService;

    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(name = "keyw", required = false) String keyw,
                        @RequestParam(name = "page", defaultValue = "1") int page) {
        Page<Product> productPage = productService.getProductsForIndex(keyw, page, 8);

        model.addAttribute("productPage", productPage);
        return "index";
    }
    @GetMapping("/product/{id}")
    public String getProductDetail(@PathVariable("id") Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "product-detail";
    }
}