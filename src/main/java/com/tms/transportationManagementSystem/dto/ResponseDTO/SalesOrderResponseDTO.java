package com.tms.transportationManagementSystem.dto.ResponseDTO;

import com.tms.transportationManagementSystem.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class SalesOrderResponseDTO {

    private Long id;

    private String orderNumber;

    private String orderType;

    private String company;

    private String customerNumber;

    private LocalDate orderDate;

    private LocalDate requestedDate;

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

    private OrderStatus orderStatus;

    private String weightUnit;

    private BigDecimal totalVolume;

    private String volumeUnit;

    private List<SalesOrderLineResponseDTO> lines;
}