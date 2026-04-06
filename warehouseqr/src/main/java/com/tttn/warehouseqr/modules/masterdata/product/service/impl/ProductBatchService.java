package com.tttn.warehouseqr.modules.masterdata.product.service.impl;

import com.tttn.warehouseqr.modules.masterdata.product.dto.ProductQrDTO;
import com.tttn.warehouseqr.modules.masterdata.product.repository.ProductBatchRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductBatchService {
    private final ProductBatchRepository productBatchRepository;

    public ProductBatchService(ProductBatchRepository productBatchRepository) {
        this.productBatchRepository = productBatchRepository;
    }

    public Page<ProductQrDTO> getBatchesWithQrCustom(int page, int limit, String keyw, long categoryId){
        Pageable pageable = PageRequest.of(page-1,limit);
        return productBatchRepository.searchBatchesWithQr(keyw,categoryId, pageable);
    }
}
