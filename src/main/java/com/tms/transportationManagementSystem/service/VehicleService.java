package com.tms.transportationManagementSystem.service;

import com.tms.transportationManagementSystem.dto.RequestDTO.AddVehicleRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.VehicleResponseDTO;

import java.util.List;

public interface VehicleService {

    VehicleResponseDTO addVehicle(AddVehicleRequestDTO requestDTO);

    List<VehicleResponseDTO> getAllVehicles();

    VehicleResponseDTO getVehicleById(Long id);

    VehicleResponseDTO updateVehicle(
            Long id,
            AddVehicleRequestDTO requestDTO
    );

    void deleteVehicle(Long id);

    List<VehicleResponseDTO> getVehiclesByTransporterId(
            Long transporterId
    );
}