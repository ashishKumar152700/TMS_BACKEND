package com.tms.transportationManagementSystem.controller;

import com.tms.transportationManagementSystem.dto.RequestDTO.VehicleRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.VehicleResponseDTO;
import com.tms.transportationManagementSystem.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    public VehicleResponseDTO createVehicle(
            @RequestBody VehicleRequestDTO requestDTO) {

        return vehicleService.createVehicle(requestDTO);
    }

    @GetMapping("/{id}")
    public VehicleResponseDTO getVehicleById(
            @PathVariable Long id) {

        return vehicleService.getVehicleById(id);
    }

    @GetMapping
    public List<VehicleResponseDTO> getAllVehicles() {

        return vehicleService.getAllVehicles();
    }

    @PutMapping("/{id}")
    public VehicleResponseDTO updateVehicle(
            @PathVariable Long id,
            @RequestBody VehicleRequestDTO requestDTO) {

        return vehicleService.updateVehicle(id, requestDTO);
    }

    @DeleteMapping("/{id}")
    public String deleteVehicle(
            @PathVariable Long id) {

        vehicleService.deleteVehicle(id);

        return "Vehicle deleted successfully";
    }
}