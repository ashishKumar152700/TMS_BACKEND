package com.tms.transportationManagementSystem.service.impl;


import com.tms.transportationManagementSystem.dto.RequestDTO.AddTransporterRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.TransporterResponseDTO;
import com.tms.transportationManagementSystem.entity.Transporter;
import com.tms.transportationManagementSystem.repository.TransporterRepository;
import com.tms.transportationManagementSystem.service.TransporterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransporterServiceImpl implements TransporterService {

    private final TransporterRepository transporterRepository;

    @Override
    public TransporterResponseDTO addTransporter(AddTransporterRequestDTO requestDTO) {

//        if (transporterRepository.existsByEmail(requestDTO.getEmail())) {
//            throw new RuntimeException("Email already exists");
//        }
//
//        if (transporterRepository.existsByMobile(requestDTO.getMobile())) {
//            throw new RuntimeException("Mobile already exists");
//        }
//
//        if (transporterRepository.existsByGstNumber(requestDTO.getGstNumber())) {
//            throw new RuntimeException("GST already exists");
//        }
//
//        if (transporterRepository.existsByPanNumber(requestDTO.getPanNumber())) {
//            throw new RuntimeException("PAN already exists");
//        }

        Transporter transporter = Transporter.builder()
                .companyName(requestDTO.getCompanyName())
                .gstNumber(requestDTO.getGstNumber())
                .panNumber(requestDTO.getPanNumber())
                .contactPerson(requestDTO.getContactPerson())
                .mobile(requestDTO.getMobile())
                .email(requestDTO.getEmail())
                .address(requestDTO.getAddress())
                .city(requestDTO.getCity())
                .state(requestDTO.getState())
                .pincode(requestDTO.getPincode())
                .status("ACTIVE")
                .build();

        Transporter savedTransporter =
                transporterRepository.save(transporter);

        return mapToDTO(savedTransporter);
    }

    @Override
    public List<TransporterResponseDTO> getAllTransporters() {

        return transporterRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public TransporterResponseDTO getTransporterById(Long id) {

        Transporter transporter = transporterRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Transporter not found"));

        return mapToDTO(transporter);
    }

    @Override
    public TransporterResponseDTO updateTransporter(Long id,
                                                    AddTransporterRequestDTO requestDTO) {

        Transporter transporter = transporterRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Transporter not found"));

        transporter.setCompanyName(requestDTO.getCompanyName());
        transporter.setGstNumber(requestDTO.getGstNumber());
        transporter.setPanNumber(requestDTO.getPanNumber());
        transporter.setContactPerson(requestDTO.getContactPerson());
        transporter.setMobile(requestDTO.getMobile());
        transporter.setEmail(requestDTO.getEmail());
        transporter.setAddress(requestDTO.getAddress());
        transporter.setCity(requestDTO.getCity());
        transporter.setState(requestDTO.getState());
        transporter.setPincode(requestDTO.getPincode());

        Transporter updatedTransporter =
                transporterRepository.save(transporter);

        return mapToDTO(updatedTransporter);
    }

    @Override
    public void deleteTransporter(Long id) {

        Transporter transporter = transporterRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Transporter not found"));

        transporterRepository.delete(transporter);
    }

    private TransporterResponseDTO mapToDTO(Transporter transporter) {

        return TransporterResponseDTO.builder()
                .id(transporter.getId())
                .companyName(transporter.getCompanyName())
                .gstNumber(transporter.getGstNumber())
                .panNumber(transporter.getPanNumber())
                .contactPerson(transporter.getContactPerson())
                .mobile(transporter.getMobile())
                .email(transporter.getEmail())
                .address(transporter.getAddress())
                .city(transporter.getCity())
                .state(transporter.getState())
                .pincode(transporter.getPincode())
                .status(transporter.getStatus())
                .build();
    }
}