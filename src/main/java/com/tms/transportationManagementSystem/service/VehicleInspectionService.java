package com.tms.transportationManagementSystem.service;

import com.tms.transportationManagementSystem.dto.RequestDTO.VehicleInspectionRequestDTO;
import com.tms.transportationManagementSystem.dto.RequestDTO.VehicleInspectionReviewRequestDTO;
import com.tms.transportationManagementSystem.dto.RequestDTO.GuardVerificationRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.VehicleInspectionAdminResponseDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.VehicleInspectionResponseDTO;

import java.io.IOException;
import java.util.List;

public interface VehicleInspectionService {

    VehicleInspectionResponseDTO createInspection(
            VehicleInspectionRequestDTO requestDTO
    ) throws IOException;

    VehicleInspectionResponseDTO getInspectionById(
            Long id
    );

    List<VehicleInspectionResponseDTO> getAllInspections();

    List<VehicleInspectionAdminResponseDTO> getAdminInspections(
            String status
    );

    VehicleInspectionAdminResponseDTO getAdminInspectionById(
            Long id
    );

    VehicleInspectionResponseDTO reviewInspection(
            Long id,
            VehicleInspectionReviewRequestDTO requestDTO
    );

    List<VehicleInspectionResponseDTO> getPendingReviews();

    List<VehicleInspectionResponseDTO> getGuardPendingInspections();

    VehicleInspectionResponseDTO verifyVehicleAtGate(Long id, GuardVerificationRequestDTO requestDTO);
}