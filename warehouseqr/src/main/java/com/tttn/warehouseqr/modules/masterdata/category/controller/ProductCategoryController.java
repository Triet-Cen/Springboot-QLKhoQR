package com.tttn.warehouseqr.modules.masterdata.category.controller;

import com.tttn.warehouseqr.modules.masterdata.category.dto.CategoryDTO;
import com.tttn.warehouseqr.modules.masterdata.category.entity.ProductCategory;
import com.tttn.warehouseqr.modules.masterdata.category.service.impl.CategoryService;
import com.tttn.warehouseqr.modules.masterdata.product.dto.ProductPageResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categories")
public class ProductCategoryController {
    private final CategoryService categoryService;

    public ProductCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String listCategories(@RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "10") int limit,
                                 @RequestParam(defaultValue = "") String keyw,
                                 Model model) {
        Page<CategoryDTO> catePage = categoryService.getCategoryCustom(page, limit, keyw);
        ProductPageResponse response = new ProductPageResponse();
        response.setContent(catePage);
        response.setCurrentPage(page);
        response.setTotalPage(catePage.getTotalPages());
        response.setTotalElements(catePage.getTotalElements());

        model.addAttribute("catePage", response);
        model.addAttribute("keyw", keyw);
        return "masterdata/categories/category-list/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("categoryDTO", new CategoryDTO());
        return "masterdata/categories/category-form/create-form";
    }

    @PostMapping("/create")
    public String createCategory(@ModelAttribute("categoryDTO") CategoryDTO categoryDTO) {
        categoryService.createCategory(categoryDTO);
        return "redirect:/categories";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") long categoryId, Model model) {
        ProductCategory category = categoryService.getCategoryById(categoryId);

        CategoryDTO dto = new CategoryDTO();
        dto.setCategoryId(category.getCategoryId());
        dto.setCategoryCode(category.getCategoryCode());
        dto.setCategoryName(category.getCategoryName());

        model.addAttribute("category", category);
        model.addAttribute("categoryDTO", dto);

        return "masterdata/categories/category-form/edit-form";
    }

    @PostMapping("/edit/{id}")
    public String updateCategory(@PathVariable("id") Long categoryId,
                                 @ModelAttribute("categoryDTO") CategoryDTO categoryDTO) {
        categoryService.updateCategory(categoryId, categoryDTO);

        return "redirect:/categories";
    }

    @PostMapping("/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return "redirect:/categories";
    }
}
