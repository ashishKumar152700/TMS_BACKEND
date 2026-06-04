package com.tms.transportationManagementSystem.integration.jde.dto;

import lombok.Data;

@Data
public class JdeTransporterResponseDTO {

    private String companyName;
    private String gstNumber;
    private String panNumber;
    private String contactPerson;
    private String mobile;
    private String email;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String active;
    private String remarks;
    private Long addressNumber;
}