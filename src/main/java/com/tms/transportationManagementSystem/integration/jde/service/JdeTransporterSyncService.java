package com.tms.transportationManagementSystem.integration.jde.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tms.transportationManagementSystem.entity.Transporter;
import com.tms.transportationManagementSystem.integration.jde.client.JdeTransporterClient;
import com.tms.transportationManagementSystem.integration.jde.dto.JdeTransporterApiResponse;
import com.tms.transportationManagementSystem.integration.jde.dto.JdeTransporterResponseDTO;
import com.tms.transportationManagementSystem.repository.TransporterRepository;
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
    private String clean(String value) {

        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        return value.trim();
    }

    public void syncTransporters() {


        try {


            String response =
                    client.getTransporters();
//            log.info("JDE RESPONSE = {}", response);
            JdeTransporterApiResponse apiResponse =
                    objectMapper.readValue(
                            response,
                            JdeTransporterApiResponse.class
                    );

            List<JdeTransporterResponseDTO> transporters =
                    apiResponse.getTransporters();

            for (JdeTransporterResponseDTO dto : transporters) {

                Transporter transporter =
                        transporterRepository
                                .findByAddressNumber(
                                        dto.getAddressNumber()
                                )
                                .orElse(new Transporter());

                transporter.setCompanyName(dto.getCompanyName());
                transporter.setGstNumber(dto.getGstNumber());
                transporter.setPanNumber(dto.getPanNumber());
                transporter.setContactPerson(dto.getContactPerson());
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
                transporter.setAddressNumber(dto.getAddressNumber());

                transporter.setStatus(
                        Boolean.TRUE.equals(dto.getActive())
                                ? "ACTIVE"
                                : "INACTIVE"
                );

                transporterRepository.save(transporter);
            }

            log.info("Transporter Sync Completed");

        } catch (Exception ex) {

            log.error(
                    "Error while syncing transporters",
                    ex
            );
        }
    }
}