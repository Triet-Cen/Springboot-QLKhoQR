package com.tttn.warehouseqr.modules.masterdata.product.dto;

import com.tttn.warehouseqr.modules.masterdata.product.entity.Product;
import org.springframework.data.domain.Page;

import java.util.List;

public class ProductPageResponse {
    private Page<Product> content;
    private int currentPage;
    private int totalPage;
    private long totalElements;

    public Page<Product> getContent() {
        return content;
    }

    public void setContent(Page<Product> content) {
        this.content = content;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }
}
