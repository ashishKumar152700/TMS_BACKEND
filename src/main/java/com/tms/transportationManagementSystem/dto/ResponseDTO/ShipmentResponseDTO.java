package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ShipmentResponseDTO {

    private Long shipmentId;

    private String shipmentNo;

    private Double totalWeight;

    private Double totalVolume;

    private String vehicleType;

    private Integer orderCount;

    private String status;
}