package com.tms.transportationManagementSystem.integration.jde.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tms.transportationManagementSystem.entity.Transporter;
import com.tms.transportationManagementSystem.entity.Vehicle;
import com.tms.transportationManagementSystem.integration.jde.client.JdeTransporterClient;
import com.tms.transportationManagementSystem.integration.jde.dto.JdeTransporterApiResponse;
import com.tms.transportationManagementSystem.integration.jde.dto.JdeTransporterResponseDTO;
import com.tms.transportationManagementSystem.integration.jde.dto.response.JdeVehicleResponseDTO;
import com.tms.transportationManagementSystem.repository.TransporterRepository;
import com.tms.transportationManagementSystem.repository.VehicleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class JdeTransporterSyncService {

    private final JdeTransporterClient client;
    private final ObjectMapper objectMapper;
    private final TransporterRepository transporterRepository;
    private final VehicleRepository vehicleRepository;

    private String clean(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        return value.trim();
    }

    public void syncTransporters() {
        try {
            String response = client.getTransporters();
            JdeTransporterApiResponse apiResponse =
                    objectMapper.readValue(response, JdeTransporterApiResponse.class);

            List<JdeTransporterResponseDTO> transporters = apiResponse.getTransporters();

            if (transporters == null || transporters.isEmpty()) {
                log.info("No transporters found in JDE response");
                return;
            }

            for (JdeTransporterResponseDTO dto : transporters) {
                Transporter transporter = transporterRepository
                        .findByAddressNumber(dto.getAddressNumber())
                        .orElse(new Transporter());

                transporter.setAddressNumber(dto.getAddressNumber());
                transporter.setCompanyName(clean(dto.getCompanyName()));
                transporter.setGstNumber(clean(dto.getGstNumber()));
                transporter.setPanNumber(clean(dto.getPanNumber()));
                transporter.setContactPerson(clean(dto.getContactPerson()));
                transporter.setMobile(clean(dto.getMobile()));
                transporter.setEmail(clean(dto.getEmail()));
                transporter.setAddress(clean(dto.getAddress()));
                transporter.setCity(clean(dto.getCity()));
                transporter.setState(clean(dto.getState()));
                transporter.setPincode(clean(dto.getPincode()));
                transporter.setRemarks(clean(dto.getRemarks()));
                transporter.setStatus(
                        "true".equalsIgnoreCase(dto.getActive()) ||
                                "Y".equalsIgnoreCase(dto.getActive()) ||
                                "1".equals(dto.getActive()) ? "ACTIVE" : "INACTIVE"
                );

                transporter = transporterRepository.save(transporter);

                // Delete old vehicles
                vehicleRepository.deleteByTransporterId(transporter.getId());

                // Save new vehicles only if present
                if (dto.getVehicles() != null && !dto.getVehicles().isEmpty()) {
                    for (JdeVehicleResponseDTO vehicleDto : dto.getVehicles()) {
                        Vehicle vehicle = new Vehicle();
                        vehicle.setVehicleNumber(clean(vehicleDto.getVehicleNumber()));
                        vehicle.setVehicleType(clean(vehicleDto.getVehicleType()));
                        vehicle.setMaxWeightCapacity(vehicleDto.getMaxWeightCapacity());
                        vehicle.setMaxVolumeCapacity(vehicleDto.getMaxVolumeCapacity());
                        vehicle.setGpsDeviceId(clean(vehicleDto.getGpsDeviceId()));
                        vehicle.setStatus(clean(vehicleDto.getStatus()));
                        vehicle.setTransporter(transporter);

                        vehicleRepository.save(vehicle);
                    }
                } else {
                    log.info("No vehicles found for transporter addressNumber={}", dto.getAddressNumber());
                }
            }

            log.info("Transporter and Vehicle Sync Completed");

        } catch (Exception ex) {
            log.error("Error while syncing transporters and vehicles", ex);
        }
    }
}