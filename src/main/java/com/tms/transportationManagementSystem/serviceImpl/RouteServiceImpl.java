package com.tms.transportationManagementSystem.serviceImpl;

import com.tms.transportationManagementSystem.dto.RequestDTO.AddRouteRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.RouteResponseDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.RouteStopResponseDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.TransporterRouteDetailsDTO;
import com.tms.transportationManagementSystem.entity.Route;
import com.tms.transportationManagementSystem.entity.TransporterRoute;
import com.tms.transportationManagementSystem.repository.RouteRepository;
import com.tms.transportationManagementSystem.repository.TransporterRouteRepository;
import com.tms.transportationManagementSystem.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;

    private final TransporterRouteRepository
            transporterRouteRepository;

    @Override
    public List<TransporterRouteDetailsDTO>
    getRoutesByTransporter(Long transporterId) {

        List<TransporterRoute> transporterRoutes =
                transporterRouteRepository
                        .findByTransporterId(transporterId);

        return transporterRoutes.stream()
                .map(transporterRoute -> {

                    Route route =
                            transporterRoute.getRoute();

                    return TransporterRouteDetailsDTO
                            .builder()
                            .routeId(route.getId())
                            .routeCode(route.getRouteCode())
                            .sourceLocation(
                                    route.getSourceLocation()
                            )
                            .destinationLocation(
                                    route.getDestinationLocation()
                            )
                            .distance(route.getDistance())
                            .estimatedTime(
                                    route.getEstimatedTime()
                            )
                            .stops(
                                    route.getRouteStops()
                                            == null
                                            ? List.of()
                                            : route.getRouteStops()
                                            .stream()
                                            .map(stop ->
                                                    RouteStopResponseDTO
                                                            .builder()
                                                            .id(stop.getId())
                                                            .stopName(
                                                                    stop.getStopName()
                                                            )
                                                            .distanceFromSource(
                                                                    stop.getDistanceFromSource()
                                                            )
                                                            .routeId(
                                                                    route.getId()
                                                            )
                                                            .routeCode(
                                                                    route.getRouteCode()
                                                            )
                                                            .build()
                                            )
                                            .toList()
                            )
                            .build();
                })
                .toList();
    }

    @Override
    public RouteResponseDTO addRoute(
            AddRouteRequestDTO requestDTO
    ) {

        if (routeRepository.existsByRouteCode(
                requestDTO.getRouteCode()
        )) {

            throw new RuntimeException(
                    "Route code already exists"
            );
        }

        Route route = Route.builder()
                .routeCode(requestDTO.getRouteCode())
                .sourceLocation(
                        requestDTO.getSourceLocation()
                )
                .destinationLocation(
                        requestDTO.getDestinationLocation()
                )
                .distance(requestDTO.getDistance())
                .estimatedTime(
                        requestDTO.getEstimatedTime()
                )
                .build();

        Route savedRoute =
                routeRepository.save(route);

        return mapToDTO(savedRoute);
    }

    @Override
    public List<RouteResponseDTO> getAllRoutes() {

        return routeRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public RouteResponseDTO getRouteById(Long id) {

        Route route =
                routeRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Route not found"
                                )
                        );

        return mapToDTO(route);
    }

    @Override
    public RouteResponseDTO updateRoute(
            Long id,
            AddRouteRequestDTO requestDTO
    ) {

        Route route =
                routeRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Route not found"
                                )
                        );

        route.setRouteCode(
                requestDTO.getRouteCode()
        );

        route.setSourceLocation(
                requestDTO.getSourceLocation()
        );

        route.setDestinationLocation(
                requestDTO.getDestinationLocation()
        );

        route.setDistance(
                requestDTO.getDistance()
        );

        route.setEstimatedTime(
                requestDTO.getEstimatedTime()
        );

        Route updatedRoute =
                routeRepository.save(route);

        return mapToDTO(updatedRoute);
    }

    @Override
    public void deleteRoute(Long id) {

        Route route =
                routeRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Route not found"
                                )
                        );

        routeRepository.delete(route);
    }

    @Override
    public List<RouteResponseDTO>
    getSuggestedRoutes(
            String pickup,
            String delivery
    ) {

        List<Route> routes =
                routeRepository
                        .findBySourceLocationAndDestinationLocation(
                                pickup,
                                delivery
                        );

        return routes.stream()
                .map(this::mapToDTO)
                .toList();
    }

    private RouteResponseDTO mapToDTO(
            Route route
    ) {

        return RouteResponseDTO.builder()
                .id(route.getId())
                .routeCode(route.getRouteCode())
                .sourceLocation(
                        route.getSourceLocation()
                )
                .destinationLocation(
                        route.getDestinationLocation()
                )
                .distance(route.getDistance())
                .estimatedTime(
                        route.getEstimatedTime()
                )
                .stops(
                        route.getRouteStops() == null
                                ? List.of()
                                : route.getRouteStops()
                                .stream()
                                .map(stop ->
                                        RouteStopResponseDTO
                                                .builder()
                                                .id(stop.getId())
                                                .stopName(
                                                        stop.getStopName()
                                                )
                                                .distanceFromSource(
                                                        stop.getDistanceFromSource()
                                                )
                                                .routeId(
                                                        route.getId()
                                                )
                                                .routeCode(
                                                        route.getRouteCode()
                                                )
                                                .build()
                                )
                                .toList()
                )
                .build();
    }
}