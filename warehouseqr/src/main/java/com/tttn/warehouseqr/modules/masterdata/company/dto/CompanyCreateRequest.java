package com.tttn.warehouseqr.modules.masterdata.company.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CompanyCreateRequest {
    @NotBlank(message = "COMPANY_NAME_REQUIRED")
    @Size(max = 255, message = "COMPANY_NAME_TOO_LONG")
    private String companyName;

    @Size(max = 50, message = "TAX_CODE_TOO_LONG")
    private String taxCode;

    @Size(max = 20, message = "PHONE_TOO_LONG")
    private String phone;

    @Email(message = "EMAIL_INVALID")
    @Size(max = 100, message = "EMAIL_TOO_LONG")
    private String email;

    private String address;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

