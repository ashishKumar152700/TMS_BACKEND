package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JdeCustomerResponseDTO {

    private Long id;
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
    private LocalDateTime syncTime;
}