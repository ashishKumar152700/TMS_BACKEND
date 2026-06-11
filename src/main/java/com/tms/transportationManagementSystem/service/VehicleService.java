package com.tms.transportationManagementSystem.service;

import com.tms.transportationManagementSystem.dto.RequestDTO.VehicleRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.VehicleResponseDTO;

import java.util.List;

public interface VehicleService {

    VehicleResponseDTO createVehicle(VehicleRequestDTO requestDTO);

    VehicleResponseDTO getVehicleById(Long id);

    List<VehicleResponseDTO> getAllVehicles();

    VehicleResponseDTO updateVehicle(Long id,
                                     VehicleRequestDTO requestDTO);

    void deleteVehicle(Long id);

}