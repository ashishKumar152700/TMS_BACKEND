package com.tms.transportationManagementSystem.serviceImpl;


import com.tms.transportationManagementSystem.dto.RequestDTO.AddMultipleRouteStopsRequestDTO;
import com.tms.transportationManagementSystem.dto.RequestDTO.AddRouteStopRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.RouteStopResponseDTO;
import com.tms.transportationManagementSystem.entity.Route;
import com.tms.transportationManagementSystem.entity.RouteStop;
import com.tms.transportationManagementSystem.repository.RouteRepository;
import com.tms.transportationManagementSystem.repository.RouteStopRepository;
import com.tms.transportationManagementSystem.service.RouteStopService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RouteStopServiceImpl implements RouteStopService {

    private final RouteStopRepository routeStopRepository;
    private final RouteRepository routeRepository;


    @Override
    public List<RouteStopResponseDTO> addRouteStops(
            Long routeId,
            List<AddMultipleRouteStopsRequestDTO> requestDTOList
    ) {

        Route route = routeRepository.findById(routeId)
                .orElseThrow(() ->
                        new RuntimeException("Route not found"));

        List<RouteStop> routeStops = new ArrayList<>();

        for (AddMultipleRouteStopsRequestDTO requestDTO
                : requestDTOList) {

            RouteStop routeStop = RouteStop.builder()
                    .stopName(requestDTO.getStopName())
                    .distanceFromSource(
                            requestDTO.getDistanceFromSource()
                    )
                    .route(route)
                    .build();

            routeStops.add(routeStop);
        }

        List<RouteStop> savedRouteStops =
                routeStopRepository.saveAll(routeStops);

        return savedRouteStops.stream()
                .map(this::mapToDTO)
                .toList();
    }



    @Override
    public RouteStopResponseDTO addRouteStop(
            AddRouteStopRequestDTO requestDTO) {

        Route route = routeRepository.findById(
                        requestDTO.getRouteId())
                .orElseThrow(() ->
                        new RuntimeException("Route not found"));

        RouteStop routeStop = RouteStop.builder()
                .stopName(requestDTO.getStopName())
//                .sequenceNo(requestDTO.getSequenceNo())
                .distanceFromSource(
                        requestDTO.getDistanceFromSource())
                .route(route)
                .build();

        RouteStop savedRouteStop =
                routeStopRepository.save(routeStop);

        return mapToDTO(savedRouteStop);
    }

    @Override
    public List<RouteStopResponseDTO> getAllRouteStops() {

        return routeStopRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public RouteStopResponseDTO getRouteStopById(Long id) {

        RouteStop routeStop = routeStopRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Route stop not found"));

        return mapToDTO(routeStop);
    }

    @Override
    public RouteStopResponseDTO updateRouteStop(
            Long id,
            AddRouteStopRequestDTO requestDTO) {

        RouteStop routeStop = routeStopRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Route stop not found"));

        Route route = routeRepository.findById(
                        requestDTO.getRouteId())
                .orElseThrow(() ->
                        new RuntimeException("Route not found"));

        routeStop.setStopName(requestDTO.getStopName());
//        routeStop.setSequenceNo(requestDTO.getSequenceNo());
        routeStop.setDistanceFromSource(
                requestDTO.getDistanceFromSource());
        routeStop.setRoute(route);

        RouteStop updatedRouteStop =
                routeStopRepository.save(routeStop);

        return mapToDTO(updatedRouteStop);
    }

    @Override
    public void deleteRouteStop(Long id) {

        RouteStop routeStop = routeStopRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Route stop not found"));

        routeStopRepository.delete(routeStop);
    }

    private RouteStopResponseDTO mapToDTO(
            RouteStop routeStop) {

        return RouteStopResponseDTO.builder()
                .id(routeStop.getId())
                .stopName(routeStop.getStopName())
//                .sequenceNo(routeStop.getSequenceNo())
                .distanceFromSource(
                        routeStop.getDistanceFromSource())
                .routeId(routeStop.getRoute().getId())
                .routeCode(routeStop.getRoute().getRouteCode())
                .build();
    }
}