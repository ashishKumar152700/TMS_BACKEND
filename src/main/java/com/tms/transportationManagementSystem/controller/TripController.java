package com.tms.transportationManagementSystem.controller;

import com.tms.transportationManagementSystem.dto.RequestDTO.TripRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.AvailableVehicleResponseDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.TripResponseDTO;
import com.tms.transportationManagementSystem.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;


    @GetMapping("/shipment/{shipmentId}/trip-id")
    public Map<String, Long> getTripIdByShipmentId(
            @PathVariable Long shipmentId) {

        return tripService.getTripIdByShipmentId(shipmentId);
    }

    @PostMapping("/assign-vehicle")
    public TripResponseDTO assignVehicle(
            @RequestBody TripRequestDTO requestDTO) {

        return tripService.createTrip(requestDTO);
    }

    @GetMapping("/{shipmentId}/available-vehicles")
    public List<AvailableVehicleResponseDTO>
    getAvailableVehicles(

            @PathVariable Long shipmentId){

        return tripService.getAvailableVehicles(shipmentId);

    }
}