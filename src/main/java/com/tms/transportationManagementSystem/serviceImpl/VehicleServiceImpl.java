package com.tms.transportationManagementSystem.service.impl;

import com.tms.transportationManagementSystem.dto.RequestDTO.AddVehicleRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.VehicleResponseDTO;
import com.tms.transportationManagementSystem.entity.Transporter;
import com.tms.transportationManagementSystem.entity.Vehicle;
import com.tms.transportationManagementSystem.repository.TransporterRepository;
import com.tms.transportationManagementSystem.repository.VehicleRepository;
import com.tms.transportationManagementSystem.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;

    private final TransporterRepository transporterRepository;

    @Override
    public List<VehicleResponseDTO> getVehiclesByTransporterId(
            Long transporterId
    ) {

        List<Vehicle> vehicles =
                vehicleRepository.findByTransporterId(
                        transporterId
                );

        return vehicles.stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public VehicleResponseDTO addVehicle(
            AddVehicleRequestDTO requestDTO
    ) {

        if (vehicleRepository.existsByVehicleNumber(
                requestDTO.getVehicleNumber()
        )) {

            throw new RuntimeException(
                    "Vehicle number already exists"
            );
        }

        Transporter transporter =
                transporterRepository.findById(
                                requestDTO.getTransporterId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Transporter not found"
                                )
                        );

        Vehicle vehicle = Vehicle.builder()
                .vehicleNumber(requestDTO.getVehicleNumber())
                .vehicleType(requestDTO.getVehicleType())
                .maxWeightCapacity(
                        requestDTO.getMaxWeightCapacity()
                )
                .maxVolumeCapacity(
                        requestDTO.getMaxVolumeCapacity()
                )
                .driverName(requestDTO.getDriverName())
                .driverPhone(requestDTO.getDriverPhone())
                .status("ACTIVE")
                .transporter(transporter)
                .build();

        Vehicle savedVehicle =
                vehicleRepository.save(vehicle);

        return mapToDTO(savedVehicle);
    }

    @Override
    public List<VehicleResponseDTO> getAllVehicles() {

        return vehicleRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public VehicleResponseDTO getVehicleById(Long id) {

        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Vehicle not found"
                        )
                );

        return mapToDTO(vehicle);
    }

    @Override
    public VehicleResponseDTO updateVehicle(
            Long id,
            AddVehicleRequestDTO requestDTO
    ) {

        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Vehicle not found"
                        )
                );

        Transporter transporter =
                transporterRepository.findById(
                                requestDTO.getTransporterId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Transporter not found"
                                )
                        );

        vehicle.setVehicleNumber(
                requestDTO.getVehicleNumber()
        );

        vehicle.setVehicleType(
                requestDTO.getVehicleType()
        );

        vehicle.setMaxWeightCapacity(
                requestDTO.getMaxWeightCapacity()
        );

        vehicle.setMaxVolumeCapacity(
                requestDTO.getMaxVolumeCapacity()
        );

        vehicle.setDriverName(
                requestDTO.getDriverName()
        );

        vehicle.setDriverPhone(
                requestDTO.getDriverPhone()
        );

        vehicle.setTransporter(transporter);

        Vehicle updatedVehicle =
                vehicleRepository.save(vehicle);

        return mapToDTO(updatedVehicle);
    }

    @Override
    public void deleteVehicle(Long id) {

        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Vehicle not found"
                        )
                );

        vehicleRepository.delete(vehicle);
    }

    private VehicleResponseDTO mapToDTO(
            Vehicle vehicle
    ) {

        return VehicleResponseDTO.builder()
                .id(vehicle.getId())
                .vehicleNumber(vehicle.getVehicleNumber())
                .vehicleType(vehicle.getVehicleType())
                .maxWeightCapacity(
                        vehicle.getMaxWeightCapacity()
                )
                .maxVolumeCapacity(
                        vehicle.getMaxVolumeCapacity()
                )
                .driverName(vehicle.getDriverName())
                .driverPhone(vehicle.getDriverPhone())
                .status(vehicle.getStatus())
                .transporterId(
                        vehicle.getTransporter().getId()
                )
                .transporterName(
                        vehicle.getTransporter().getCompanyName()
                )
                .build();
    }
}