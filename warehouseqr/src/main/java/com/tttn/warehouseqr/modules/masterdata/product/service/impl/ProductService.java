package com.tttn.warehouseqr.modules.masterdata.product.service.impl;

import com.tttn.warehouseqr.modules.masterdata.category.entity.ProductCategory;
import com.tttn.warehouseqr.modules.masterdata.category.repository.CategoryRepository;
import com.tttn.warehouseqr.modules.masterdata.product.dto.ProductDTO;
import com.tttn.warehouseqr.modules.masterdata.product.dto.ProductPageResponse;
import com.tttn.warehouseqr.modules.masterdata.product.entity.Product;
import com.tttn.warehouseqr.modules.masterdata.product.repository.ProductRepository;
import com.tttn.warehouseqr.modules.masterdata.unit.entity.Unit;
import com.tttn.warehouseqr.modules.masterdata.unit.repository.UnitRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UnitRepository unitRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository
            , UnitRepository unitRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.unitRepository = unitRepository;
    }
    public ProductPageResponse getALlProductCustom(int page, int limit, String keyw, long categoryId){
        Pageable pageable = PageRequest.of(page -1,limit);
        Page<Product> product = productRepository.getProducPageCustom(keyw,categoryId,pageable);
//        List<Product> product = productRepository.findAll();

        ProductPageResponse response = new ProductPageResponse();
        response.setContent(product);
        response.setCurrentPage(page);
        response.setTotalPage(product.getTotalPages());
        response.setTotalElements(product.getTotalElements());

        return response;
    }

    public Product getProductById(long productId){
        return productRepository.findById(productId).orElseThrow(
                () -> new RuntimeException("Không tìm thấy sản phẩm.")
        );
    }

    public Product createProduct(ProductDTO productDTO){

        ProductCategory category = categoryRepository.findById(productDTO.getCategoryId()).orElseThrow(
                () -> new RuntimeException("Không tìm thấy Category")
        );

        Unit unit = unitRepository.findById(productDTO.getUnitId()).orElseThrow(
                ()->new RuntimeException("Không timg thấu Unit")
        );

        Product product = new Product();
        product.setSku(productDTO.getSku());
        product.setProductName(productDTO.getProductName());
        product.setDescription(productDTO.getDescription());
        product.setMinStock(productDTO.getMinStock());
        product.setCategory(category);
        product.setUnit(unit);

        return productRepository.save(product);
    }

    public Product updateProdcut(long productId, ProductDTO productDTO){
        Product updatePro = productRepository.findById(productId).orElseThrow(
                () -> new RuntimeException("Không tìm thấy sản phẩm.")
        );

        if (productDTO.getCategoryId() != 0){
            ProductCategory category = categoryRepository.findById(productDTO.getCategoryId()).orElseThrow(
                    () -> new RuntimeException("Không tìm thấy Category")
            );
            updatePro.setCategory(category);
        }

        if (productDTO.getUnitId() != 0){
            Unit unit = unitRepository.findById(productDTO.getUnitId()).orElseThrow(
                    ()->new RuntimeException("Không timg thấu Unit")
            );
            updatePro.setUnit(unit);
        }

        updatePro.setSku(productDTO.getSku());
        updatePro.setProductName(productDTO.getProductName());
        updatePro.setDescription(productDTO.getDescription());
        updatePro.setMinStock(productDTO.getMinStock());

        return productRepository.save(updatePro);
    }

    public String deleteProduct (long productId){
        Product deletePro = productRepository.findById(productId).orElseThrow(
                () -> new RuntimeException("Không tìm thấy sản phẩm")
        );

        productRepository.delete(deletePro);
        return "Đã xóa sản phẩm";
    }
}
