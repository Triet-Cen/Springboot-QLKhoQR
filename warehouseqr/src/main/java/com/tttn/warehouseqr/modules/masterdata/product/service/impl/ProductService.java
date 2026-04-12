package com.tttn.warehouseqr.modules.masterdata.product.service.impl;

import com.tttn.warehouseqr.modules.inventory.entity.InventoryLocationBalance;
import com.tttn.warehouseqr.modules.inventory.repository.InventoryLocationBalanceRepository;
import com.tttn.warehouseqr.modules.masterdata.category.entity.ProductCategory;
import com.tttn.warehouseqr.modules.masterdata.category.repository.CategoryRepository;
import com.tttn.warehouseqr.modules.masterdata.product.dto.ProductDTO;
import com.tttn.warehouseqr.modules.masterdata.product.dto.ProductPageResponse;
import com.tttn.warehouseqr.modules.masterdata.product.dto.ProductScanDTO;
import com.tttn.warehouseqr.modules.masterdata.product.entity.Product;
import com.tttn.warehouseqr.modules.masterdata.product.entity.ProductBatch;
import com.tttn.warehouseqr.modules.masterdata.product.repository.ProductBatchRepository;
import com.tttn.warehouseqr.modules.masterdata.product.repository.ProductRepository;
import com.tttn.warehouseqr.modules.masterdata.unit.entity.Unit;
import com.tttn.warehouseqr.modules.masterdata.unit.repository.UnitRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UnitRepository unitRepository;
    private final ProductBatchRepository productBatchRepository;
    private final InventoryLocationBalanceRepository balanceRepo;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository
            , UnitRepository unitRepository, ProductBatchRepository productBatchRepository, InventoryLocationBalanceRepository balanceRepo) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.unitRepository = unitRepository;
        this.productBatchRepository = productBatchRepository;
        this.balanceRepo = balanceRepo;
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

    public ProductScanDTO getProductForScan(String sku, String lotCode, Long warehouseId) {
        // 1. Tìm Product
        Product product = productRepository.findBySku(sku);
        if (product == null) throw new RuntimeException("SKU " + sku + " không tồn tại!");

        // 2. Tìm Batch liên kết với Product đó
        ProductBatch batch = productBatchRepository.findByLotCodeAndProductProduct_id(lotCode, product.getProduct_id())
                .orElseThrow(() -> new RuntimeException("Lô " + lotCode + " không thuộc sản phẩm này!"));

        // 3. Tìm vị trí (location) cũ/gần nhất của lô hàng này trong kho
        // Sử dụng phương thức findFirst... bạn đã định nghĩa trong Repo
        Optional<InventoryLocationBalance> balanceOpt = balanceRepo.findFirstByWarehouseIdAndProductIdAndBatchId(
                warehouseId, product.getProduct_id(), batch.getBatchId());

        // 4. Thiết lập giá trị mặc định nếu chưa từng có tồn kho (balance == null)
        Long locationId = 1L;
        String locationCode = "Vị trí mặc định";

        if (balanceOpt.isPresent()) {
            InventoryLocationBalance balance = balanceOpt.get();
            locationId = balance.getLocationId();
            locationCode = "Kệ cũ: " + locationId; // Sau này bạn có thể join lấy locationCode xịn hơn
        }

        // 5. Đóng gói vào DTO (Đảm bảo truyền đủ 9 tham số)
        return new ProductScanDTO(
                product.getProduct_id(),
                product.getProductName(),
                batch.getBatchId(),
                batch.getLotCode(),
                product.getSku(),
                1.0,           // actualQty mặc định khi quét là 1
                locationId,    // Lấy động từ balance
                locationCode,  // Lấy động từ balance
                batch.getCostPrice() != null ? batch.getCostPrice().doubleValue() : 0.0 // Giá nhập gợi ý
        );
    }

}
