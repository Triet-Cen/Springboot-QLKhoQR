package com.tttn.warehouseqr.modules.masterdata.company.service;

import com.tttn.warehouseqr.modules.masterdata.company.dto.CompanyCreateRequest;
import com.tttn.warehouseqr.modules.masterdata.company.dto.CompanyUpdateRequest;
import com.tttn.warehouseqr.modules.masterdata.company.entity.Company;

import java.util.List;

public interface CompanyService {
    void createCompany(CompanyCreateRequest req);
    void updateCompany(CompanyUpdateRequest req);
    CompanyUpdateRequest getUpdateById(Long id);
    List<Company> getAllCompanies();
    List<Company> searchCompanies(String keyword);
    void deleteCompany(Long id);
}

