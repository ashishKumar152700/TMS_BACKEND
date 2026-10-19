package com.tms.transportationManagementSystem.service;

import com.tms.transportationManagementSystem.dto.RequestDTO.TripRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.AvailableVehicleResponseDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.TripResponseDTO;

import java.util.List;
import java.util.Map;

public interface TripService {

    TripResponseDTO createTrip(
            TripRequestDTO requestDTO
    );

    List<AvailableVehicleResponseDTO> getAvailableVehicles(Long shipmentId);

    Map<String, Long> getTripIdByShipmentId(Long shipmentId);
}