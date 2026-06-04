package com.tms.transportationManagementSystem.service.impl;


import com.tms.transportationManagementSystem.dto.RequestDTO.AddTransporterRouteRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.TransporterRouteResponseDTO;
import com.tms.transportationManagementSystem.entity.Route;
import com.tms.transportationManagementSystem.entity.Transporter;
import com.tms.transportationManagementSystem.entity.TransporterRoute;
import com.tms.transportationManagementSystem.repository.RouteRepository;
import com.tms.transportationManagementSystem.repository.TransporterRepository;
import com.tms.transportationManagementSystem.repository.TransporterRouteRepository;
import com.tms.transportationManagementSystem.service.TransporterRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransporterRouteServiceImpl
        implements TransporterRouteService {

    private final TransporterRouteRepository transporterRouteRepository;

    private final TransporterRepository transporterRepository;

    private final RouteRepository routeRepository;

    @Override
    public TransporterRouteResponseDTO addTransporterRoute(
            AddTransporterRouteRequestDTO requestDTO) {

        boolean exists =
                transporterRouteRepository
                        .existsByTransporterIdAndRouteId(
                                requestDTO.getTransporterId(),
                                requestDTO.getRouteId());

        if (exists) {
            throw new RuntimeException(
                    "Mapping already exists");
        }

        Transporter transporter =
                transporterRepository.findById(
                                requestDTO.getTransporterId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Transporter not found"));

        Route route =
                routeRepository.findById(
                                requestDTO.getRouteId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Route not found"));

        TransporterRoute transporterRoute =
                TransporterRoute.builder()
                        .transporter(transporter)
                        .route(route)
                        .pricePreference(
                                requestDTO.getPricePreference())
                        .active(true)
                        .build();

        TransporterRoute savedMapping =
                transporterRouteRepository
                        .save(transporterRoute);

        return mapToDTO(savedMapping);
    }

    @Override
    public List<TransporterRouteResponseDTO> getAllMappings() {

        return transporterRouteRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public TransporterRouteResponseDTO getMappingById(Long id) {

        TransporterRoute transporterRoute =
                transporterRouteRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Mapping not found"));

        return mapToDTO(transporterRoute);
    }

    @Override
    public TransporterRouteResponseDTO updateMapping(
            Long id,
            AddTransporterRouteRequestDTO requestDTO) {

        TransporterRoute transporterRoute =
                transporterRouteRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Mapping not found"));

        Transporter transporter =
                transporterRepository.findById(
                                requestDTO.getTransporterId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Transporter not found"));

        Route route =
                routeRepository.findById(
                                requestDTO.getRouteId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Route not found"));

        transporterRoute.setTransporter(transporter);
        transporterRoute.setRoute(route);
        transporterRoute.setPricePreference(
                requestDTO.getPricePreference());

        TransporterRoute updatedMapping =
                transporterRouteRepository
                        .save(transporterRoute);

        return mapToDTO(updatedMapping);
    }

    @Override
    public void deleteMapping(Long id) {

        TransporterRoute transporterRoute =
                transporterRouteRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Mapping not found"));

        transporterRouteRepository.delete(transporterRoute);
    }

    private TransporterRouteResponseDTO mapToDTO(
            TransporterRoute transporterRoute) {

        return TransporterRouteResponseDTO.builder()
                .id(transporterRoute.getId())
                .transporterId(
                        transporterRoute.getTransporter().getId())
                .transporterName(
                        transporterRoute.getTransporter()
                                .getCompanyName())
                .routeId(
                        transporterRoute.getRoute().getId())
                .routeCode(
                        transporterRoute.getRoute()
                                .getRouteCode())
                .pricePreference(
                        transporterRoute.getPricePreference())
                .active(transporterRoute.getActive())
                .build();
    }
}