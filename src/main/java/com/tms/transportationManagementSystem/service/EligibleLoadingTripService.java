package com.tms.transportationManagementSystem.service;

import com.tms.transportationManagementSystem.dto.ResponseDTO.EligibleLoadingTripResponseDTO;

import java.util.List;

public interface EligibleLoadingTripService {

    List<EligibleLoadingTripResponseDTO> getEligibleTrips();
}