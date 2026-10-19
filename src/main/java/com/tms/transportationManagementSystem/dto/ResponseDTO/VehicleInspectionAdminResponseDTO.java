package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class VehicleInspectionAdminResponseDTO {

    private VehicleInspectionShipmentResponseDTO shipment;

    private List<SalesOrderResponseDTO> salesOrders;

    private VehicleInspectionTransporterResponseDTO transporter;

    private VehicleInspectionResponseDTO vehicleInspection;
}