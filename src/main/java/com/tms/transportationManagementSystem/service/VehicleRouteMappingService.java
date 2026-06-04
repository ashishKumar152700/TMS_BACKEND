package com.tms.transportationManagementSystem.service;



import com.tms.transportationManagementSystem.dto.RequestDTO.AddVehicleRouteMappingRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.VehicleRouteMappingResponseDTO;

import java.util.List;

public interface VehicleRouteMappingService {

    VehicleRouteMappingResponseDTO addMapping(
            AddVehicleRouteMappingRequestDTO requestDTO);

    List<VehicleRouteMappingResponseDTO> getAllMappings();

    VehicleRouteMappingResponseDTO getMappingById(Long id);

    void deleteMapping(Long id);
}