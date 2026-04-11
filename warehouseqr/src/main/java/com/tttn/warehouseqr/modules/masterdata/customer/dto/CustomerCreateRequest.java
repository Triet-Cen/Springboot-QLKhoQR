package com.tttn.warehouseqr.modules.masterdata.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CustomerCreateRequest {
    @NotBlank(message = "CUSTOMER_CODE_REQUIRED")
    @Size(max = 50, message = "CUSTOMER_CODE_TOO_LONG")
    private String customerCode;

    @NotBlank(message = "CUSTOMER_NAME_REQUIRED")
    @Size(max = 255, message = "CUSTOMER_NAME_TOO_LONG")
    private String customerName;

    @Size(max = 100, message = "CONTACT_PERSON_TOO_LONG")
    private String contactPerson;

    @Pattern(regexp = "^$|^(0|\\+84)(\\d{9})$", message = "PHONE_INVALID")
    private String phone;

    @Pattern(regexp = "^$|^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "EMAIL_INVALID")
    @Size(max = 100, message = "EMAIL_TOO_LONG")
    private String email;

    private String address;

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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
}

