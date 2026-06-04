package com.tms.transportationManagementSystem.service;



import com.tms.transportationManagementSystem.dto.RequestDTO.AddTransporterRouteRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.TransporterRouteResponseDTO;

import java.util.List;

public interface TransporterRouteService {

    TransporterRouteResponseDTO addTransporterRoute(
            AddTransporterRouteRequestDTO requestDTO);

    List<TransporterRouteResponseDTO> getAllMappings();

    TransporterRouteResponseDTO getMappingById(Long id);

    TransporterRouteResponseDTO updateMapping(
            Long id,
            AddTransporterRouteRequestDTO requestDTO);

    void deleteMapping(Long id);
}