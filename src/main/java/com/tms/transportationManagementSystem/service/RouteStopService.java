package com.tms.transportationManagementSystem.service;



import com.tms.transportationManagementSystem.dto.RequestDTO.AddMultipleRouteStopsRequestDTO;
import com.tms.transportationManagementSystem.dto.RequestDTO.AddRouteStopRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.RouteStopResponseDTO;

import java.util.List;

public interface RouteStopService {

    RouteStopResponseDTO addRouteStop(
            AddRouteStopRequestDTO requestDTO);

    List<RouteStopResponseDTO> getAllRouteStops();

    RouteStopResponseDTO getRouteStopById(Long id);

    RouteStopResponseDTO updateRouteStop(
            Long id,
            AddRouteStopRequestDTO requestDTO);

    void deleteRouteStop(Long id);

    List<RouteStopResponseDTO> addRouteStops(
            Long routeId,
            List<AddMultipleRouteStopsRequestDTO> requestDTOList
    );
}