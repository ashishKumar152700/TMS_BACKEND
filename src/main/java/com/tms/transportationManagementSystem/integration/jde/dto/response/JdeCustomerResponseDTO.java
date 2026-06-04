package com.tms.transportationManagementSystem.integration.jde.dto.response;

import lombok.Data;

@Data
public class JdeCustomerResponseDTO {

    private Long addressNumber;
    private String customerName;
    private String gstNumber;
    private String panNumber;
    private String contactPerson;
    private String mobile;
    private String email;
    private String address;
    private String city;
    private String state;
    private String pinCode;
    private String country;
    private String active;
    private String remarks;
}