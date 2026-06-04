package com.tms.transportationManagementSystem.service;



import com.tms.transportationManagementSystem.dto.RequestDTO.AddTransporterRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.TransporterResponseDTO;

import java.util.List;

public interface TransporterService {

    TransporterResponseDTO addTransporter(AddTransporterRequestDTO requestDTO);

    List<TransporterResponseDTO> getAllTransporters();

    TransporterResponseDTO getTransporterById(Long id);

    TransporterResponseDTO updateTransporter(Long id,
                                             AddTransporterRequestDTO requestDTO);

    void deleteTransporter(Long id);
}