package com.tms.transportationManagementSystem.dto.RequestDTO;

import lombok.Data;
@Data
public class AddTransporterRequestDTO {

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

    // Additional Fields
    private Boolean active;
    private String remarks;
    private Long addressNumber; // JDE Address Book Number
}