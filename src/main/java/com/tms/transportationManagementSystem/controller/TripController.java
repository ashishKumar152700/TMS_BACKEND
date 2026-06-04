package com.tms.transportationManagementSystem.controller;

import com.tms.transportationManagementSystem.dto.RequestDTO.TripRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.TripResponseDTO;
import com.tms.transportationManagementSystem.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trips")
public class TripController {

    @Autowired
    private TripService tripService;

    @PostMapping
    public TripResponseDTO createTrip(
            @RequestBody
            TripRequestDTO requestDTO) {

        return tripService.createTrip(requestDTO);
    }
}