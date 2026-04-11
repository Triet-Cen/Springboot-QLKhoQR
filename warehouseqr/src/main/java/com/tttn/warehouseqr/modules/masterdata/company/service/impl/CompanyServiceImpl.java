package com.tttn.warehouseqr.modules.masterdata.company.service.impl;

import com.tttn.warehouseqr.modules.masterdata.company.dto.CompanyCreateRequest;
import com.tttn.warehouseqr.modules.masterdata.company.dto.CompanyUpdateRequest;
import com.tttn.warehouseqr.modules.masterdata.company.entity.Company;
import com.tttn.warehouseqr.modules.masterdata.company.repository.CompanyRepository;
import com.tttn.warehouseqr.modules.masterdata.company.service.CompanyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    private String normalize(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    @Override
    public void createCompany(CompanyCreateRequest req) {
        String name = normalize(req.getCompanyName());
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Company name is required");
        }

        Company company = new Company();
        company.setCompanyName(name);
        company.setTaxCode(normalize(req.getTaxCode()));
        company.setPhone(normalize(req.getPhone()));
        company.setEmail(normalize(req.getEmail()));
        company.setAddress(normalize(req.getAddress()));

        companyRepository.save(company);
    }

    @Override
    public void updateCompany(CompanyUpdateRequest req) {
        Company company = companyRepository.findById(req.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company id not found"));

        String name = normalize(req.getCompanyName());
        if (name == null || name.isBlank()) {
            throw new RuntimeException("Company name is required");
        }

        company.setCompanyName(name);
        company.setTaxCode(normalize(req.getTaxCode()));
        company.setPhone(normalize(req.getPhone()));
        company.setEmail(normalize(req.getEmail()));
        company.setAddress(normalize(req.getAddress()));

        companyRepository.save(company);
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyUpdateRequest getUpdateById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company id not found"));

        CompanyUpdateRequest dto = new CompanyUpdateRequest();
        dto.setCompanyId(company.getCompanyId());
        dto.setCompanyName(company.getCompanyName());
        dto.setTaxCode(company.getTaxCode());
        dto.setPhone(company.getPhone());
        dto.setEmail(company.getEmail());
        dto.setAddress(company.getAddress());

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Company> searchCompanies(String keyword) {
        String kw = normalize(keyword);
        return companyRepository.searchByKeyword(kw);
    }

    @Override
    public void deleteCompany(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company id not found"));
        companyRepository.delete(company);
    }
}

