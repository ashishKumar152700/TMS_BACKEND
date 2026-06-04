package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ShipmentStopDTO {

    private Long id;

    private Long shipmentId;

    private String shipmentNo;

    private String stopName;

    private Integer sequenceNo;

    private LocalDateTime arrivalTime;

    private LocalDateTime departureTime;
}