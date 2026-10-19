package com.tms.transportationManagementSystem.controller;

import com.tms.transportationManagementSystem.dto.RequestDTO.VehicleInspectionRequestDTO;
import com.tms.transportationManagementSystem.dto.RequestDTO.VehicleInspectionReviewRequestDTO;
import com.tms.transportationManagementSystem.dto.RequestDTO.GuardVerificationRequestDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.VehicleInspectionAdminResponseDTO;
import com.tms.transportationManagementSystem.dto.ResponseDTO.VehicleInspectionResponseDTO;
import com.tms.transportationManagementSystem.service.VehicleInspectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/vehicle-inspections")
@RequiredArgsConstructor
public class VehicleInspectionController {

    private final VehicleInspectionService vehicleInspectionService;


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public VehicleInspectionResponseDTO createInspection(
            @ModelAttribute VehicleInspectionRequestDTO requestDTO
    ) throws IOException {

        return vehicleInspectionService.createInspection(
                requestDTO
        );
    }


    @GetMapping
    public List<VehicleInspectionResponseDTO> getAllInspections() {

        return vehicleInspectionService.getAllInspections();
    }


    // Guard: trucks awaiting physical/document verification at gate
    @GetMapping("/guard/pending")
    public List<VehicleInspectionResponseDTO> getGuardPendingInspections() {
        return vehicleInspectionService.getGuardPendingInspections();
    }

    // Guard: complete vehicle, driver, document and inspection details
    @GetMapping("/guard/{id}")
    public VehicleInspectionResponseDTO getGuardInspection(@PathVariable Long id) {
        return vehicleInspectionService.getInspectionById(id);
    }

    // Guard decision: ACCEPTED, REJECTED or ON_HOLD
    @PutMapping("/{id}/guard-verification")
    public VehicleInspectionResponseDTO verifyVehicleAtGate(
            @PathVariable Long id,
            @RequestBody GuardVerificationRequestDTO requestDTO) {
        return vehicleInspectionService.verifyVehicleAtGate(id, requestDTO);
    }


    @GetMapping("/admin")
    public List<VehicleInspectionAdminResponseDTO> getAdminInspections(
            @RequestParam(defaultValue = "ALL") String status
    ) {

        return vehicleInspectionService.getAdminInspections(
                status
        );
    }


    @GetMapping("/admin/{id}")
    public VehicleInspectionAdminResponseDTO getAdminInspection(
            @PathVariable Long id
    ) {

        return vehicleInspectionService.getAdminInspectionById(
                id
        );
    }


    @PutMapping("/{id}/review")
    public VehicleInspectionResponseDTO reviewInspection(
            @PathVariable Long id,
            @RequestBody VehicleInspectionReviewRequestDTO requestDTO
    ) {

        return vehicleInspectionService.reviewInspection(
                id,
                requestDTO
        );
    }


    @GetMapping("/{id}")
    public VehicleInspectionResponseDTO getInspection(
            @PathVariable Long id
    ) {

        return vehicleInspectionService.getInspectionById(id);
    }
}