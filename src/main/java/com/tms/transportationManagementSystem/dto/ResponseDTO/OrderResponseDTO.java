package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderResponseDTO {

    private Long id;

    private String orderNo;

    private String customerName;

    private Double totalWeight;

    private Double totalVolume;

    private String status;
}