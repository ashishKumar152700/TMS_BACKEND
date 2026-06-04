package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ShipmentBidDTO {

    private Long id;

    private Long shipmentId;

    private String shipmentNo;

    private Long transporterId;

    private String transporterName;

    private Double bidAmount;

    private String remarks;

    private String status;

    private LocalDateTime createdAt;
}