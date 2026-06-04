package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class VehicleAssignmentResponseDTO {

    private Long assignmentId;

    private Long shipmentId;

    private String shipmentNo;

    private Long vehicleId;

    private String vehicleNumber;

    private String shipmentStatus;

    private LocalDateTime assignedAt;
}