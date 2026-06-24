package com.tms.transportationManagementSystem.dto.ResponseDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class JdeSalesOrderResponseDTO {

    private Long id;

    private String orderNumber;
    private String orderType;
    private String company;
    private String customerNumber;

    private String orderDate;
    private String requestedDate;

    private String branchPlant;
    private String status;
    private String currencyCode;

    private Long shipToCustomerNumber;
    private String shipToCustomerName;
    private String shipToAddress1;
    private String shipToCity;
    private String shipToState;
    private String shipToPinCode;

    private BigDecimal totalWeight;
    private String weightUnit;

    private BigDecimal totalVolume;

    @JsonProperty("VolumeUnit")
    private String volumeUnit;

    private List<JdeSalesOrderLineDTO> lines;
}