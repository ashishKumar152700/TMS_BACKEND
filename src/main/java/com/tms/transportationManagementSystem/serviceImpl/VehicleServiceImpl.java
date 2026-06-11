package com.tms.transportationManagementSystem.serviceImpl;

import com.tms.transportationManagementSystem.dto.RequestDTO.VehicleRequestDTO;
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
    public VehicleResponseDTO createVehicle(VehicleRequestDTO dto) {

//        if (vehicleRepository.existsByVehicleNumber(dto.getVehicleNumber())) {
//            throw new RuntimeException("Vehicle already exists");
//        }

        Transporter transporter =
                transporterRepository.findById(dto.getTransporterId())
                        .orElseThrow(() ->
                                new RuntimeException("Transporter not found"));

        Vehicle vehicle = Vehicle.builder()
                .vehicleNumber(dto.getVehicleNumber())
                .vehicleType(dto.getVehicleType())
                .vehicleCategory(dto.getVehicleCategory())

                .maxWeightCapacity(dto.getMaxWeightCapacity())
                .maxVolumeCapacity(dto.getMaxVolumeCapacity())

                .gpsDeviceId(dto.getGpsDeviceId())
                .status(dto.getStatus())

                .transporter(transporter)
                .build();

        return mapToResponse(vehicleRepository.save(vehicle));
    }

    @Override
    public VehicleResponseDTO getVehicleById(Long id) {

        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Vehicle not found"));

        return mapToResponse(vehicle);
    }

    @Override
    public List<VehicleResponseDTO> getAllVehicles() {

        return vehicleRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public VehicleResponseDTO updateVehicle(Long id,
                                            VehicleRequestDTO dto) {

        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Vehicle not found"));

        Transporter transporter =
                transporterRepository.findById(dto.getTransporterId())
                        .orElseThrow(() ->
                                new RuntimeException("Transporter not found"));

        vehicle.setVehicleNumber(dto.getVehicleNumber());
        vehicle.setVehicleType(dto.getVehicleType());
        vehicle.setVehicleCategory(dto.getVehicleCategory());

        vehicle.setMaxWeightCapacity(dto.getMaxWeightCapacity());
        vehicle.setMaxVolumeCapacity(dto.getMaxVolumeCapacity());

        vehicle.setGpsDeviceId(dto.getGpsDeviceId());
        vehicle.setStatus(dto.getStatus());



        vehicle.setTransporter(transporter);

        return mapToResponse(vehicleRepository.save(vehicle));
    }

    @Override
    public void deleteVehicle(Long id) {

        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Vehicle not found"));

        vehicleRepository.delete(vehicle);
    }

    private VehicleResponseDTO mapToResponse(Vehicle vehicle) {

        return VehicleResponseDTO.builder()
                .id(vehicle.getId())
                .vehicleNumber(vehicle.getVehicleNumber())
                .vehicleType(vehicle.getVehicleType())
                .vehicleCategory(vehicle.getVehicleCategory())
                .maxWeightCapacity(vehicle.getMaxWeightCapacity())
                .maxVolumeCapacity(vehicle.getMaxVolumeCapacity())

                .gpsDeviceId(vehicle.getGpsDeviceId())
                .status(vehicle.getStatus())

                .transporterId(vehicle.getTransporter().getId())
                .transporterName(vehicle.getTransporter().getCompanyName())                .build();
    }
}