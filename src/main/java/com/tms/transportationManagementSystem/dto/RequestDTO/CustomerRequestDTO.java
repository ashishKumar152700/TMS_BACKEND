package com.tms.transportationManagementSystem.dto.RequestDTO;

import lombok.Data;

@Data
public class CustomerRequestDTO {

    private Long addressNumber;
    private String companyCode;
    private String alphaName;
    private Integer cm;
    private String longAddress;
    private String industryClass;
    private String scheduleType;
    private String individualTaxId;
}