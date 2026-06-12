package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Data;

import java.util.List;

@Data
public class JdeCustomerResponseDTO {

    private Long id;
    private Long addressNumber;
    private String alphaName;
    private Integer cm;
    private String companyCode;
    private String individualTaxId;
    private String industryClass;
    private String longAddress;
    private String scheduleType;

    private List<JdeSalesOrderResponseDTO> salesOrders;
}