package com.tms.transportationManagementSystem.dto.ResponseDTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ShipmentDTO {

    private Long id;

    private String shipmentNo;

    private LocalDate plannedDate;

    private Double totalWeight;

    private Double totalVolume;

    // NEW
    private String selectedVehicleType;

    // NEW
    private Integer selectedVehicleCount;

    private String status;
}