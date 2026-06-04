package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerResponseDTO {

    private Long id;
    private Long addressNumber;
    private String companyCode;
    private String alphaName;
    private Integer cm;
    private String longAddress;
    private String industryClass;
    private String scheduleType;
    private String individualTaxId;
}