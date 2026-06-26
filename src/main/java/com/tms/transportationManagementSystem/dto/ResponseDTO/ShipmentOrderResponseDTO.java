package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class ShipmentOrderResponseDTO {

    private Long orderId;

    private String orderNumber;

    private String customerNumber;

    private String customerName;

    private String city;

    private String state;

    private BigDecimal totalWeight;

    private BigDecimal totalVolume;

    private LocalDate orderDate;

    private String orderStatus;
}