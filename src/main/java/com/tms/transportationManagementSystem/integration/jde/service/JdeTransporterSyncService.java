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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JdeTransporterSyncService {

    private final JdeTransporterClient client;
    private final ObjectMapper objectMapper;
    private final TransporterRepository transporterRepository;
    private final VehicleRepository vehicleRepository;
    private final TransactionTemplate transactionTemplate;

    private String clean(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        return value.trim();
    }

    public void syncTransporters() {
        try {
            // JDE call transaction ke bahar - slow API ke time DB connection hold nahi hota
            String response = client.getTransporters();
            JdeTransporterApiResponse apiResponse =
                    objectMapper.readValue(response, JdeTransporterApiResponse.class);

            List<JdeTransporterResponseDTO> transporters = apiResponse.getTransporters();

            if (transporters == null || transporters.isEmpty()) {
                return;
            }

            // Saara DB kaam pehle jaisa ek hi transaction mein
            transactionTemplate.executeWithoutResult(status -> saveNewRecords(transporters));

        } catch (Exception ex) {
            log.error("Error while syncing transporters and vehicles", ex);
        }
    }

    private void saveNewRecords(List<JdeTransporterResponseDTO> transporters) {

        // Existing transporters aur vehicle numbers ek saath (pehle har row ke liye findBy...)
        Map<Long, Transporter> transportersByAddress =
                transporterRepository.findByAddressNumberIn(
                                transporters.stream()
                                        .map(JdeTransporterResponseDTO::getAddressNumber)
                                        .filter(Objects::nonNull)
                                        .collect(Collectors.toSet()))
                        .stream()
                        .collect(Collectors.toMap(
                                Transporter::getAddressNumber,
                                Function.identity()));

        Set<String> existingVehicleNumbers =
                new HashSet<>(vehicleRepository.findAllVehicleNumbers());

        List<Vehicle> newVehicles = new ArrayList<>();
        int insertedTransporters = 0;

        for (JdeTransporterResponseDTO dto : transporters) {

            if (dto.getVehicles() != null) {
                dto.getVehicles().forEach(v ->
                        log.debug("Vehicle Number: {}", v.getVehicleNumber()));
            }

            // Transporter DB mein pehle se hai to usko touch nahi karna
            // (TMS mein kiye gaye changes overwrite nahi honge), sirf naya insert hoga.
            Transporter transporter = transportersByAddress.get(dto.getAddressNumber());

            if (transporter == null) {

                transporter = new Transporter();

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

                // Isi response mein same transporter dobara aaye to yahi reuse hoga
                transportersByAddress.put(transporter.getAddressNumber(), transporter);
                insertedTransporters++;
            }

            if (dto.getVehicles() != null && !dto.getVehicles().isEmpty()) {

                for (JdeVehicleResponseDTO vehicleDto : dto.getVehicles()) {

                    String vehicleNumber = clean(vehicleDto.getVehicleNumber());

                    // Vehicle pehle se hai (DB ya isi response mein) to skip, sirf naya insert hoga
                    if (!existingVehicleNumbers.add(vehicleNumber)) {
                        continue;
                    }

                    Vehicle vehicle = new Vehicle();

                    vehicle.setVehicleNumber(vehicleNumber);
                    vehicle.setVehicleType(clean(vehicleDto.getVehicleType()));
                    vehicle.setMaxWeightCapacity(vehicleDto.getMaxWeightCapacity());
                    vehicle.setMaxVolumeCapacity(vehicleDto.getMaxVolumeCapacity());
                    vehicle.setWeightUnit(clean(vehicleDto.getTotalWeight()));
                    vehicle.setVolumeUnit(clean(vehicleDto.getTotalVolume()));
                    vehicle.setGpsDeviceId(clean(vehicleDto.getGpsDeviceId()));
                    vehicle.setStatus(clean(vehicleDto.getStatus()));
                    vehicle.setTransporter(transporter);

                    newVehicles.add(vehicle);
                }
            }
        }

        vehicleRepository.saveAll(newVehicles);

        log.info("Transporter Sync Completed. Total Records : {} | New Transporters : {} | New Vehicles : {}",
                transporters.size(), insertedTransporters, newVehicles.size());
    }
}
