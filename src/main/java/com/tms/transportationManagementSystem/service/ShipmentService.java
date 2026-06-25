package com.tms.transportationManagementSystem.service;

import com.tms.transportationManagementSystem.dto.RequestDTO.ShipmentCreateRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.ShipmentResponseDTO;

public interface ShipmentService {

    ShipmentResponseDTO createShipment(
            ShipmentCreateRequestDTO request);
}