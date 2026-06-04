package com.tms.transportationManagementSystem.service;

import com.tms.transportationManagementSystem.dto.RequestDTO.VehicleAssignmentRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.VehicleAssignmentResponseDTO;

public interface VehicleAssignmentService {

    VehicleAssignmentResponseDTO assignVehicle(
            Long shipmentId,
            VehicleAssignmentRequestDTO requestDTO
    );
}