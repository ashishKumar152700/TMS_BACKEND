package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Data
@Getter
@Setter
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
    private String volumeUnit;

    private List<JdeSalesOrderLineDTO> lines;
}