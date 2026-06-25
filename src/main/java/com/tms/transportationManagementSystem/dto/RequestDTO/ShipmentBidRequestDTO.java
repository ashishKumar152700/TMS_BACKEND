package com.tms.transportationManagementSystem.dto.RequestDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShipmentBidRequestDTO {

    private Long shipmentId;
    private Long transporterId;
    private Double bidAmount;
    private Integer vehicleCount;
    private String remarks;
}