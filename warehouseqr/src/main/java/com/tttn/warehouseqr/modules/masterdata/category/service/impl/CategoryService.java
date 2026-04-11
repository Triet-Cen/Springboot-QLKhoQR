package com.tttn.warehouseqr.modules.masterdata.category.service.impl;

import com.tttn.warehouseqr.modules.masterdata.category.dto.CategoryDTO;
import com.tttn.warehouseqr.modules.masterdata.category.entity.ProductCategory;
import com.tttn.warehouseqr.modules.masterdata.category.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Page<CategoryDTO> getCategoryCustom(int page, int limit, String keyw){
        Pageable pageable = PageRequest.of(page -1,limit);

        return categoryRepository.categoryPage(keyw,pageable);
    }

    public  List<ProductCategory> getAllCategory(){
        List<ProductCategory> categories = categoryRepository.findAll();

        return  categories;
    }

    public ProductCategory getCategoryById(long id){
        ProductCategory category = categoryRepository.findById(id).orElseThrow(
                ()-> new RuntimeException("Không tìm thấy Danh Mục")
        );
        return category;
    }
    public ProductCategory createCategory(CategoryDTO categoryDTO){
        ProductCategory category = new ProductCategory();
        category.setCategoryCode(categoryDTO.getCategoryCode());
        category.setCategoryName(categoryDTO.getCategoryName());

        return categoryRepository.save(category);
    }

    public ProductCategory updateCategory(long id, CategoryDTO categoryDTO){
        ProductCategory category = categoryRepository.findById(id).orElseThrow(
                ()-> new RuntimeException("không tìm thấy mã danh mục.")
        );
        category.setCategoryCode(categoryDTO.getCategoryCode());
        category.setCategoryName(categoryDTO.getCategoryName());

        return categoryRepository.save(category);
    }

    public void deleteCategory(long id){
        ProductCategory category = categoryRepository.findById(id).orElseThrow(
                ()-> new RuntimeException("không tìm thấy mã danh mục.")
        );

        if(!category.getProducts().isEmpty()){
            throw new RuntimeException("Danh mục đang tồn tại sản phẩm. Không thể xóa!");
        }
        else {
            categoryRepository.delete(category);
        }
    }
}
