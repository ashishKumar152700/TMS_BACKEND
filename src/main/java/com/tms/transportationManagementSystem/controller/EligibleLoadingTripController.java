package com.tms.transportationManagementSystem.controller;

import com.tms.transportationManagementSystem.dto.ResponseDTO.EligibleLoadingTripResponseDTO;
import com.tms.transportationManagementSystem.service.EligibleLoadingTripService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/loading-confirmations")
@RequiredArgsConstructor
public class EligibleLoadingTripController {

    private final EligibleLoadingTripService eligibleLoadingTripService;

    @GetMapping("/eligible-trips")
    public List<EligibleLoadingTripResponseDTO> getEligibleTrips() {
        return eligibleLoadingTripService.getEligibleTrips();
    }
}