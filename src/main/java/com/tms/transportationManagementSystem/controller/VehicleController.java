package com.tms.transportationManagementSystem.controller;

import com.tms.transportationManagementSystem.dto.RequestDTO.AddVehicleRequestDTO;
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
    public VehicleResponseDTO addVehicle(
            @RequestBody AddVehicleRequestDTO requestDTO
    ) {

        return vehicleService.addVehicle(requestDTO);
    }

    @GetMapping("/transporter/{transporterId}")
    public List<VehicleResponseDTO> getVehiclesByTransporterId(
            @PathVariable Long transporterId
    ) {

        return vehicleService.getVehiclesByTransporterId(
                transporterId
        );
    }

    @GetMapping
    public List<VehicleResponseDTO> getAllVehicles() {

        return vehicleService.getAllVehicles();
    }

    @GetMapping("/{id}")
    public VehicleResponseDTO getVehicleById(
            @PathVariable Long id
    ) {

        return vehicleService.getVehicleById(id);
    }

    @PutMapping("/{id}")
    public VehicleResponseDTO updateVehicle(
            @PathVariable Long id,
            @RequestBody AddVehicleRequestDTO requestDTO
    ) {

        return vehicleService.updateVehicle(id, requestDTO);
    }

    @DeleteMapping("/{id}")
    public String deleteVehicle(@PathVariable Long id) {

        vehicleService.deleteVehicle(id);

        return "Vehicle deleted successfully";
    }
}