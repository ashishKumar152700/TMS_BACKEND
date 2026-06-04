package com.tms.transportationManagementSystem.controller;


import com.tms.transportationManagementSystem.dto.RequestDTO.AddVehicleRouteMappingRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.VehicleRouteMappingResponseDTO;
import com.tms.transportationManagementSystem.service.VehicleRouteMappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicle-route-mappings")
@RequiredArgsConstructor
public class VehicleRouteMappingController {

    private final VehicleRouteMappingService
            vehicleRouteMappingService;

    @PostMapping
    public VehicleRouteMappingResponseDTO addMapping(
            @RequestBody
            AddVehicleRouteMappingRequestDTO requestDTO) {

        return vehicleRouteMappingService
                .addMapping(requestDTO);
    }

    @GetMapping
    public List<VehicleRouteMappingResponseDTO>
    getAllMappings() {

        return vehicleRouteMappingService
                .getAllMappings();
    }

    @GetMapping("/{id}")
    public VehicleRouteMappingResponseDTO
    getMappingById(@PathVariable Long id) {

        return vehicleRouteMappingService
                .getMappingById(id);
    }

    @DeleteMapping("/{id}")
    public String deleteMapping(
            @PathVariable Long id) {

        vehicleRouteMappingService.deleteMapping(id);

        return "Vehicle Route Mapping deleted successfully";
    }
}