package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleAssignmentResponseDTO {

    private Long tripId;

    private String tripNo;

    private Long shipmentId;

    private Long vehicleId;

    private String vehicleNumber;

    private String driverName;

    private String driverMobile;

    private String status;

    private String message;

}