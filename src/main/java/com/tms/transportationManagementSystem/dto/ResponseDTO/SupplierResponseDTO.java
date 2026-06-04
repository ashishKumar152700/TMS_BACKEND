package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SupplierResponseDTO {

    private Long id;
    private Long addressNumber;
    private String alphaName;
    private String individualTaxId;
    private String longAddress;
    private String industryClass;
    private String scheduleType;
    private String taxId;
}