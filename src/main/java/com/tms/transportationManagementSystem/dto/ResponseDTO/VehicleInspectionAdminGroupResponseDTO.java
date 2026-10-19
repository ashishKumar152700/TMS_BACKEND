package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class VehicleInspectionAdminGroupResponseDTO {

    private VehicleInspectionTransporterResponseDTO transporter;

    private List<VehicleInspectionAdminResponseDTO> inspections;
}