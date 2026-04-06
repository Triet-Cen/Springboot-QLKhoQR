package com.tttn.warehouseqr.modules.masterdata.product.controller;

import com.tttn.warehouseqr.modules.masterdata.category.entity.ProductCategory;
import com.tttn.warehouseqr.modules.masterdata.category.service.impl.CategoryService;
import com.tttn.warehouseqr.modules.masterdata.product.dto.ProductDTO;
import com.tttn.warehouseqr.modules.masterdata.product.dto.ProductPageResponse;
import com.tttn.warehouseqr.modules.masterdata.product.entity.Product;
import com.tttn.warehouseqr.modules.masterdata.product.service.impl.ProductService;
import com.tttn.warehouseqr.modules.masterdata.unit.entity.Unit;
import com.tttn.warehouseqr.modules.masterdata.unit.service.impl.UnitService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;
    private final CategoryService categoryService;
    private final UnitService unitService;

    public ProductController(ProductService productService, CategoryService categoryService, UnitService unitService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.unitService = unitService;
    }

    @GetMapping
    public String listProduct(@RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "10") int limit,
                              @RequestParam(defaultValue = "") String keyw,
                              @RequestParam(defaultValue = "0") long categoryId,
                              Model model){
        ProductPageResponse response = productService.getALlProductCustom(page,limit,keyw,categoryId);
        model.addAttribute("productPage", response);
        model.addAttribute("keyword", keyw);

        return "productAndQr/products/product-list/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model){
        List<ProductCategory> categories = categoryService.getAllCategory();
        List<Unit> units = unitService.getAllUnit();
        model.addAttribute("categories",categories);
        model.addAttribute("units",units);
        model.addAttribute("productDTO", new ProductDTO());

        return "productAndQr/products/product-form/create-form";
    }
    @PostMapping("/create")
    public String createProduct(@ModelAttribute("productDTO") ProductDTO productDTO){
        productService.createProduct(productDTO);

        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditFrom(@PathVariable("id") long productId, Model model){
        Product product = productService.getProductById(productId);
        List<ProductCategory> categories = categoryService.getAllCategory();
        List<Unit> units = unitService.getAllUnit();

        ProductDTO dto = new ProductDTO();
        dto.setSku(product.getSku());
        dto.setProductName(product.getProductName());
        dto.setDescription(product.getDescription());
        dto.setMinStock(product.getMinStock());

        if(product.getCategory() != null){
            dto.setCategoryId(product.getCategory().getCategoryId());
        }
        if(product.getUnit() != null){
            dto.setUnitId(product.getUnit().getUnitId());
        }

        model.addAttribute("categories",categories);
        model.addAttribute("units",units);
        model.addAttribute("productDTO" , dto);
        model.addAttribute("product_id",productId);
        return "productAndQr/products/product-form/edit-form";
    }
    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable("id") long productId, @ModelAttribute("productDTO") ProductDTO productDTO){
        productService.updateProdcut(productId, productDTO);
        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") long productId){
        productService.deleteProduct(productId);

        return "redirect:/products";
    }

}
