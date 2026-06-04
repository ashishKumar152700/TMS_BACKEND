package com.tms.transportationManagementSystem.dto.RequestDTO;

import lombok.Data;

@Data
public class SupplierRequestDTO {

    private Long addressNumber;
    private String alphaName;
    private String individualTaxId;
    private String longAddress;
    private String industryClass;
    private String scheduleType;
    private String taxId;
}