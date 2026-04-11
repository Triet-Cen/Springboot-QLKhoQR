package com.tttn.warehouseqr.modules.masterdata.product.service.impl;

import com.tttn.warehouseqr.modules.inventory.repository.InventoryLocationBalanceRepository;
import com.tttn.warehouseqr.modules.masterdata.product.dto.ProductDTO;
import com.tttn.warehouseqr.modules.masterdata.product.dto.ProductQrDTO;
import com.tttn.warehouseqr.modules.masterdata.product.entity.Product;
import com.tttn.warehouseqr.modules.masterdata.product.repository.ProductBatchRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductBatchService {
    private final ProductBatchRepository productBatchRepository;
    private final InventoryLocationBalanceRepository inventoryLocationBalanceRepository;

    public ProductBatchService(ProductBatchRepository productBatchRepository,
                               InventoryLocationBalanceRepository inventoryLocationBalanceRepository) {
        this.productBatchRepository = productBatchRepository;
        this.inventoryLocationBalanceRepository = inventoryLocationBalanceRepository;
    }

    public Page<ProductQrDTO> getBatchesWithQrCustom(int page, int limit, String keyw, long categoryId){
        Pageable pageable = PageRequest.of(page-1,limit);
        Page<ProductQrDTO> pageData = productBatchRepository.searchBatchesWithQr(keyw,categoryId, pageable);

        for (ProductQrDTO dto : pageData.getContent()){
            List<Object[]> inventoryData = inventoryLocationBalanceRepository.getStockAndLocationByBatchId(dto.getBatchId());
            double totalQty = 0;
            List<String> location = new ArrayList<>();

            for (Object[] row : inventoryData){
                if (row[0] != null){
                    totalQty += ((Number) row[0]).doubleValue();
                }
                if (row[1] != null){
                    location.add((String) row[1]);
                }
            }
            dto.setQuantity(totalQty);
            if (totalQty>0){
                dto.setLocation(String.join(", ",location));
            }
            else {
                dto.setLocation("Chưa nhập");
            }

        }
        return pageData;
    }
}
