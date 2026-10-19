package com.tms.transportationManagementSystem.dto.ResponseDTO;

import com.tms.transportationManagementSystem.enums.ShipmentStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class VehicleInspectionShipmentResponseDTO {

    private Long id;

    private String shipmentNo;

    private Long routeId;

    private LocalDate plannedDate;

    private Double totalWeight;

    private Double totalVolume;

    private String selectedVehicleType;

    private Integer selectedVehicleCount;

    private ShipmentStatus status;

    private Long vehicleId;

    private Double expectedPrice;

    private LocalDate plannedPickupDate;

    private String bidMessage;
}