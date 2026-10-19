package com.tms.transportationManagementSystem.serviceImpl;


import com.tms.transportationManagementSystem.dto.RequestDTO.AddVehicleRouteMappingRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.VehicleRouteMappingResponseDTO;
import com.tms.transportationManagementSystem.entity.Route;
import com.tms.transportationManagementSystem.entity.Vehicle;
import com.tms.transportationManagementSystem.entity.VehicleRouteMapping;
import com.tms.transportationManagementSystem.repository.RouteRepository;
import com.tms.transportationManagementSystem.repository.VehicleRepository;
import com.tms.transportationManagementSystem.repository.VehicleRouteMappingRepository;
import com.tms.transportationManagementSystem.service.VehicleRouteMappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleRouteMappingServiceImpl
        implements VehicleRouteMappingService {

    private final VehicleRouteMappingRepository
            vehicleRouteMappingRepository;

    private final VehicleRepository vehicleRepository;

    private final RouteRepository routeRepository;

    @Override
    public VehicleRouteMappingResponseDTO addMapping(
            AddVehicleRouteMappingRequestDTO requestDTO) {

        boolean exists =
                vehicleRouteMappingRepository
                        .existsByVehicleIdAndRouteId(
                                requestDTO.getVehicleId(),
                                requestDTO.getRouteId());

        if (exists) {
            throw new RuntimeException(
                    "Mapping already exists");
        }

        Vehicle vehicle =
                vehicleRepository.findById(
                                requestDTO.getVehicleId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Vehicle not found"));

        Route route =
                routeRepository.findById(
                                requestDTO.getRouteId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Route not found"));

        VehicleRouteMapping mapping =
                VehicleRouteMapping.builder()
                        .vehicle(vehicle)
                        .route(route)
                        .active(true)
                        .build();

        VehicleRouteMapping savedMapping =
                vehicleRouteMappingRepository.save(mapping);

        return mapToDTO(savedMapping);
    }

    @Override
    public List<VehicleRouteMappingResponseDTO>
    getAllMappings() {

        return vehicleRouteMappingRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public VehicleRouteMappingResponseDTO
    getMappingById(Long id) {

        VehicleRouteMapping mapping =
                vehicleRouteMappingRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Mapping not found"));

        return mapToDTO(mapping);
    }

    @Override
    public void deleteMapping(Long id) {

        VehicleRouteMapping mapping =
                vehicleRouteMappingRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Mapping not found"));

        vehicleRouteMappingRepository.delete(mapping);
    }

    private VehicleRouteMappingResponseDTO mapToDTO(
            VehicleRouteMapping mapping) {

        return VehicleRouteMappingResponseDTO.builder()
                .id(mapping.getId())
                .vehicleId(mapping.getVehicle().getId())
                .vehicleNumber(
                        mapping.getVehicle()
                                .getVehicleNumber())
                .routeId(mapping.getRoute().getId())
                .routeCode(
                        mapping.getRoute()
                                .getRouteCode())
                .active(mapping.getActive())
                .build();
    }
}