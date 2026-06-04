package com.tms.transportationManagementSystem.service;



import com.tms.transportationManagementSystem.dto.RequestDTO.AddRouteRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.RouteResponseDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.TransporterRouteDetailsDTO;

import java.util.List;

public interface RouteService {

    RouteResponseDTO addRoute(AddRouteRequestDTO requestDTO);

    List<RouteResponseDTO> getAllRoutes();

    RouteResponseDTO getRouteById(Long id);

    RouteResponseDTO updateRoute(Long id,
                                 AddRouteRequestDTO requestDTO);

    void deleteRoute(Long id);

    List<RouteResponseDTO> getSuggestedRoutes(
            String pickup,
            String delivery
    );

    List<TransporterRouteDetailsDTO>
    getRoutesByTransporter(Long transporterId);

}