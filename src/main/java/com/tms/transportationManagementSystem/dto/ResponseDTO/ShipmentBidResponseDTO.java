package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ShipmentBidResponseDTO {

    private Long bidId;
    private Long shipmentId;

    private Long transporterId;
    private String transporterName;

    private Double bidAmount;
    private Integer vehicleCount;

    private String remarks;
    private Boolean accepted;
}