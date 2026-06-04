package com.tms.transportationManagementSystem.service;

import com.tms.transportationManagementSystem.dto.RequestDTO.TripRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.TripResponseDTO;

public interface TripService {

    TripResponseDTO createTrip(
            TripRequestDTO requestDTO
    );
}