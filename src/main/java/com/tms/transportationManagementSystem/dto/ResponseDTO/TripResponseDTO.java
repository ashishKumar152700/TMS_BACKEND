package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TripResponseDTO {

    private Long tripId;

    private String tripNo;

    private Long shipmentId;

    private String shipmentNo;

    private Long vehicleId;

    private String vehicleNumber;

    private String driverName;

    private String status;

    private LocalDateTime startTime;

}