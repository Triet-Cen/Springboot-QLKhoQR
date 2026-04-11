package com.tttn.warehouseqr.modules.masterdata.supplier.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class SupplierUpdateRequest {
    @NotNull(message = "ID_REQUIRED")
    private Long supplierId;

    @Size(max = 50, message = "SUPPLIER_CODE_TOO_LONG")
    private String supplierCode;

    @Size(max = 255, message = "SUPPLIER_NAME_TOO_LONG")
    private String supplierName;

    @Size(max = 100, message = "CONTACT_PERSON_TOO_LONG")
    private String contactPerson;

    @Pattern(regexp = "^$|^(0|\\+84)(\\d{9})$", message = "PHONE_INVALID")
    private String phone;

    @Email(message = "EMAIL_INVALID")
    @Size(max = 100, message = "EMAIL_TOO_LONG")
    private String email;

    private String address;

    private boolean active;

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
