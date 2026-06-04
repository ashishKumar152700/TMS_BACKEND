package com.tms.transportationManagementSystem.dto.RequestDTO;

import lombok.Data;

@Data
public class JdeCustomerRequestDTO {

    private String addressNumber;
    private String customerName;
    private String searchType;
    private String taxId;
    private String phoneNumber;
    private String email;
    private String addressLine1;
    private String city;
    private String state;
    private String postalCode;
    private String country;
}